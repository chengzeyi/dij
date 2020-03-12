package pers.cheng.dij.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
                                       String cwd,
                                       String[] envVars) throws IOException, IllegalConnectorArgumentsException, VMStartException {
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

        EventRequestManager manager = debugSession.getVM().get
    }
}
