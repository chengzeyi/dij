package pers.cheng.dij.runner;

import com.sun.jdi.*;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.connect.LaunchingConnector;
import pers.cheng.dij.core.Configuration;

import java.util.logging.Logger;

/**
 * JDIDebugger
 */
public class JDIDebugger {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private Class debugClass;
    private String[] classPaths;
    private int[] breakPointLines;

    public VirtualMachine connectAndLaunchVM() throws Exception {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        var arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        StringBuilder options = new StringBuilder();
        for (String classPath : classPaths) {
            options.append(" -cp \"").append(classPath).append("\"");
        }
        arguments.get("options").setValue(options.toString());
        LOGGER.info(String.format("Launching VM, args: %s", arguments));
        return launchingConnector.launch(arguments);
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
        LOGGER.info(String.format("Class prepare request enabled, class filter is %s", debugClass.getName()));
    }

//    public void setBreakPoints(VirtualMachine vm, ClassPrepareEvent event) throws SetBreakPointsException {
//        ClassType classType;
//        try {
//            classType = (ClassType) event.referenceType();
//        } catch (ClassCastException e) {
//            throw new SetBreakPointsException(String.format("Cannot cast class %s to %s", event.referenceType().getClass().getName(), ClassType.class.getName()), breakPointLines);
//        }
//        if (!classType.name().equals(debugClass.getName())) {
//            throw new SetBreakPointsException(String.format("Unmatched class %s, require %s", classType.name(), debugClass.getName()), breakPointLines);
//        }
//        for (int lineNumber : breakPointLines) {
//            Location location;
//            try {
//                List<Location> locations = classType.locationsOfLine(lineNumber);
//                if (locations.isEmpty()) {
//                    LOGGER.warning(String.format("Locations are empty at line %d", lineNumber));
//                    continue;
//                }
//                location = classType.locationsOfLine(lineNumber).get(0);
//            } catch (AbsentInformationException e) {
//                LOGGER.warning(String.format("Missing necessary information at line %d", lineNumber));
//                continue;
//            }
//            BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
//            bpReq.enable();
//            LOGGER.info(String.format("Break point enabled at line %d", lineNumber));
//        }
//    }
//
//    public void displayVariables(LocatableEvent event) throws DisplayVariablesException {
//        StackFrame stackFrame;
//        try {
//            stackFrame = event.thread().frame(0);
//        } catch (IncompatibleThreadStateException e) {
//            throw new DisplayVariablesException("Thread is in incompatible state", e.getCause());
//        }
//
//        if (stackFrame.location().toString().contains(debugClass.getName())) {
//            try {
//                var visableVariables = stackFrame.getValues(stackFrame.visibleVariables());
//                System.out.println("Variable at " + stackFrame.location().toString() + " > ");
//                for (var entry : visableVariables.entrySet()) {
//                    System.out.println(entry.getKey().name() + " = " + entry.getValue());
//                }
//            } catch (AbsentInformationException e) {
//                throw new DisplayVariablesException("Missing necessary Information", e.getCause());
//            }
//        }
//    }
//
//    public void enableStepRequest(VirtualMachine vm, BreakpointEvent event) {
//        if (event.location().toString().contains(debugClass.getName() + ":" + breakPointLines[breakPointLines.length - 1])) {
//            StepRequest stepRequest = vm.eventRequestManager().createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
//            stepRequest.enable();
//        }
//    }
//
//    public void setDebugClass(Class debugClass) {
//        this.debugClass = debugClass;
//    }
//
//    public void setClassPaths(String[] classPaths) {
//        this.classPaths = classPaths;
//    }
//
//    public void setBreakPointLines(int[] breakPointLines) {
//        this.breakPointLines = breakPointLines;
//    }
}