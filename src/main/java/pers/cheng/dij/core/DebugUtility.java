package pers.cheng.dij.core;

import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.StepRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DebugUtility {
    public static final String HOME = "home";
    public static final String OPTIONS = "options";
    public static final String MAIN = "main";
    public static final String SUSPEND = "suspend";
    public static final String QUOTE = "quote";
    public static final String EXEC = "vmexec";
    public static final String CWD = "cwd";
    // ENV is not used.
    public static final String ENV = "env";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";
    public static final String TIMEOUT = "timeout";

    public static IDebugSession launch(VirtualMachineManager vmManager,
            String mainClass,
            String programArguments,
            String vmArguments,
            List<String> modulePaths,
            List<String> classPaths,
            String cwd) throws IOException, IllegalConnectorArgumentsException, VMStartException {
        return DebugUtility.launch(vmManager,
                mainClass,
                programArguments,
                vmArguments,
                String.join(File.pathSeparator, modulePaths),
                String.join(File.pathSeparator, classPaths),
                cwd);
    }

    public static IDebugSession launch(VirtualMachineManager vmManager,
            String mainClass,
            String programArguments,
            String vmArguments,
            String modulePaths,
            String classPaths,
            String cwd)
            throws IOException, IllegalConnectorArgumentsException, VMStartException {
        return launch(vmManager, mainClass, programArguments, vmArguments, modulePaths, classPaths, cwd, null);
    }

    public static IDebugSession launch(VirtualMachineManager vmManager,
            String mainClass,
            String programArguments,
            String vmArguments,
            List<String> modulePaths,
            List<String> classPaths,
            String cwd,
            String javaExec)
            throws IOException, IllegalConnectorArgumentsException, VMStartException {
        return DebugUtility.launch(vmManager,
                mainClass,
                programArguments,
                vmArguments,
                String.join(File.pathSeparator, modulePaths),
                String.join(File.pathSeparator, classPaths),
                cwd,
                javaExec);
    }

    public static IDebugSession launch(VirtualMachineManager vmManager,
                                       String mainClass,
                                       String programArguments,
                                       String vmArguments,
                                       String modulePaths,
                                       String classPaths,
                                       String cwd,
                                       String javaExec) throws IOException, IllegalConnectorArgumentsException, VMStartException {

        LaunchingConnector connector = vmManager.defaultConnector();

        var arguments = connector.defaultArguments();
        arguments.get(SUSPEND).setValue("true");

        StringBuilder optionsBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(vmArguments)) {
            optionsBuilder.append(vmArguments);
        }
        if (StringUtils.isNotBlank(modulePaths)) {
            optionsBuilder.append(" --module-path \"").append(modulePaths).append("\"");
        }
        if (StringUtils.isNotBlank(classPaths)) {
            optionsBuilder.append(" -cp \"").append(classPaths).append("\"");
        }
        arguments.get(OPTIONS).setValue(optionsBuilder.toString());

        if (mainClass.split("/").length == 2) {
            mainClass = "-m " + mainClass;
        }
        if (StringUtils.isNotBlank(programArguments)) {
            mainClass += " "+ programArguments;
        }
        arguments.get(MAIN).setValue(mainClass);

        if (arguments.get(CWD) != null) {
            arguments.get(CWD).setValue(cwd);
        }

        if (isValidJavaExec(javaExec)) {
            String vmExec = new File(javaExec).getName();
            String javaHome = new File(javaExec).getParentFile().getParentFile().getAbsolutePath();
            arguments.get(HOME).setValue(javaHome);
            arguments.get(EXEC).setValue(vmExec);
        } else if (StringUtils.isNotEmpty(DebugSettings.getCurrent().getJavaHome())) {
            arguments.get(HOME).setValue(DebugSettings.getCurrent().getJavaHome());
        }

        VirtualMachine vm = connector.launch(arguments);
        return new DebugSession(vm);
    }

    private static boolean isValidJavaExec(String javaExec) {
        if (StringUtils.isBlank(javaExec)) {
            return false;
        }

        File file = new File(javaExec);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        return Files.isExecutable(file.toPath()) &&
                Objects.equals(file.getParentFile().getName(), "bin");
    }

    public static StepRequest createStepOverRequest(ThreadReference thread, String[] stepFilters) {
        return createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER, stepFilters);
    }

    public static StepRequest createStepOutRequest(ThreadReference thread, String[] stepFilters) {
        return createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_OUT, stepFilters);
    }

    private static StepRequest createStepRequest(ThreadReference thread, int stepSize, int stepDepth, String[] stepFilters) {
        StepRequest request = thread.virtualMachine().eventRequestManager().createStepRequest(thread, stepSize, stepDepth);
        if (stepFilters != null) {
            for (String stepFilter : stepFilters) {
                request.addClassExclusionFilter(stepFilter);
            }
        }
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        request.addCountFilter(1);

        return request;
    }

    public static CompletableFuture<Long> stopOnEntry(IDebugSession debugSession, String mainClass) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        EventRequestManager manager = debugSession.getVM().eventRequestManager();
        MethodEntryRequest request = manager.createMethodEntryRequest();
        request.addClassFilter(mainClass);
        request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);

        debugSession.getEventHub().getEvents().filter(debugEvent -> {
            return debugEvent.getEvent() instanceof MethodEntryEvent &&
                request.equals(debugEvent.getEvent().request());
        }).subscribe(debugEvent -> {
            Method method = ((MethodEntryEvent) debugEvent.getEvent()).method();
            if (method.isPublic() && method.isStatic() && method.name().equals("main") && method.signature().equals("([Ljava/lang/String;)V")) {
                deleteEventRequestSafely(debugSession.getVM().eventRequestManager(), request);
                debugEvent.setShouldResume(false);
                ThreadReference bpThread = ((MethodEntryEvent) debugEvent.getEvent()).thread();
                // Tell the future to complete.
                future.complete(bpThread.uniqueID());
            }
        });
        request.enable();

        return future;
    }

    public static ThreadReference getThread(IDebugSession debugSession, long threadId) {
        for (ThreadReference thread : getAllThreadsSafely(debugSession)) {
            if (thread.uniqueID() == threadId && !thread.isCollected()) {
                return thread;
            }
        }
        return null;
    }

    public static List<ThreadReference> getAllThreadsSafely(IDebugSession debugSession) {
        if (debugSession != null) {
            try {
                return debugSession.getAllThreads();
            } catch (VMDisconnectedException e) {
                // do nothing.
            }
        }
        return new ArrayList<>();
    }

    public static void resumeThread(ThreadReference thread) {
        if (thread == null || thread.isCollected()) {
            return;
        }
        try {
            int suspends = thread.suspendCount();
            for (int i = 0; i < suspends; ++i) {
                thread.resume();
            }
        } catch (ObjectCollectedException e) {
            // do nothing.
        }
    }

    public static void deleteEventRequestSafely(EventRequestManager eventManager, EventRequest request) {
        try {
            eventManager.deleteEventRequest(request);
        } catch (VMDisconnectedException e) {
            // ignore.
        }
    }

    public static void deleteEventRequestSafely(EventRequestManager eventManager, List<EventRequest> requests) {
        try {
            eventManager.deleteEventRequests(requests);
        } catch (VMDisconnectedException e) {
            // ignore.
        }
    }

    public static String encodeArrayArgument(String[] argument) {
        if (argument == null) {
            return null;
        }

        List<String> encodedArgs = new ArrayList<>();
        for (String arg : argument) {
            try {
                encodedArgs.add(URLEncoder.encode(arg, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                // do nothing.
            }
        }
        return String.join("\n", encodedArgs);
    }

    public static String[] decodeArrayArgument(String argument) {
        if (argument == null) {
            return null;
        }

        List<String> result = new ArrayList<>();
        String[] splits = argument.split("\n");
        for (String split : splits) {
            try {
                result.add(URLDecoder.decode(split, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                // do nothing.
            }
        }

        return result.toArray(new String[0]);
    }

    public static List<String> parseArguments(String cmdStr) {
        if (cmdStr == null) {
            return new ArrayList<>();
        }

        return System.getProperty("os.name", "").toLowerCase().contains("win") ? parseArgumentsWindows(cmdStr) : parseArgumentsNonWindows(cmdStr);
    }

    private static List<String> parseArgumentsNonWindows(String args) {
        // man sh, see topic QUOTING
        List<String> result = new ArrayList<>();

        final int DEFAULT = 0;
        final int ARG = 1;
        final int IN_DOUBLE_QUOTE = 2;
        final int IN_SINGLE_QUOTE = 3;

        int state = DEFAULT;
        StringBuilder buf = new StringBuilder();
        int len = args.length();
        for (int i = 0; i < len; i++) {
            char ch = args.charAt(i);
            if (Character.isWhitespace(ch)) {
                if (state == DEFAULT) {
                    // skip
                    continue;
                } else if (state == ARG) {
                    state = DEFAULT;
                    result.add(buf.toString());
                    buf.setLength(0);
                    continue;
                }
            }
            switch (state) {
                case DEFAULT:
                case ARG:
                    if (ch == '"') {
                        state = IN_DOUBLE_QUOTE;
                    } else if (ch == '\'') {
                        state = IN_SINGLE_QUOTE;
                    } else if (ch == '\\' && i + 1 < len) {
                        state = ARG;
                        ch = args.charAt(++i);
                        buf.append(ch);
                    } else {
                        state = ARG;
                        buf.append(ch);
                    }
                    break;

                case IN_DOUBLE_QUOTE:
                    if (ch == '"') {
                        state = ARG;
                    } else if (ch == '\\' && i + 1 < len && (args.charAt(i + 1) == '\\' || args.charAt(i + 1) == '"')) {
                        ch = args.charAt(++i);
                        buf.append(ch);
                    } else {
                        buf.append(ch);
                    }
                    break;

                case IN_SINGLE_QUOTE:
                    if (ch == '\'') {
                        state = ARG;
                    } else {
                        buf.append(ch);
                    }
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
        if (buf.length() > 0 || state != DEFAULT) {
            result.add(buf.toString());
        }

        return result;
    }

    private static List<String> parseArgumentsWindows(String args) {
        // see http://msdn.microsoft.com/en-us/library/a1y7w461.aspx
        List<String> result = new ArrayList<>();
        final int DEFAULT = 0;
        final int ARG = 1;
        final int IN_DOUBLE_QUOTE = 2;

        int state = DEFAULT;
        int backslashes = 0;
        StringBuilder buf = new StringBuilder();
        int len = args.length();
        for (int i = 0; i < len; i++) {
            char ch = args.charAt(i);
            if (ch == '\\') {
                backslashes++;
                continue;
            } else if (backslashes != 0) {
                if (ch == '"') {
                    for (; backslashes >= 2; backslashes -= 2) {
                        buf.append('\\');
                    }
                    if (backslashes == 1) {
                        if (state == DEFAULT) {
                            state = ARG;
                        }
                        buf.append('"');
                        backslashes = 0;
                        continue;
                    } // else fall through to switch
                } else {
                    // false alarm, treat passed backslashes literally...
                    if (state == DEFAULT) {
                        state = ARG;
                    }
                    for (; backslashes > 0; backslashes--) {
                        buf.append('\\');
                    }
                    // fall through to switch
                }
            }
            if (Character.isWhitespace(ch)) {
                if (state == DEFAULT) {
                    // skip
                    continue;
                } else if (state == ARG) {
                    state = DEFAULT;
                    result.add(buf.toString());
                    buf.setLength(0);
                    continue;
                }
            }
            switch (state) {
                case DEFAULT:
                case ARG:
                    if (ch == '"') {
                        state = IN_DOUBLE_QUOTE;
                    } else {
                        state = ARG;
                        buf.append(ch);
                    }
                    break;

                case IN_DOUBLE_QUOTE:
                    if (ch == '"') {
                        if (i + 1 < len && args.charAt(i + 1) == '"') {
                            /* Undocumented feature in Windows:
                             * Two consecutive double quotes inside a double-quoted argument are interpreted as
                             * a single double quote.
                             */
                            buf.append('"');
                            i++;
                        } else if (buf.length() == 0) {
                            // empty string on Windows platform. Account for bug in constructor of JDK's java.lang.ProcessImpl.
                            result.add("\"\""); //$NON-NLS-1$
                            state = DEFAULT;
                        } else {
                            state = ARG;
                        }
                    } else {
                        buf.append(ch);
                    }
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
        if (buf.length() > 0 || state != DEFAULT) {
            result.add(buf.toString());
        }
        return result;
    }
}
