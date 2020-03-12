package pers.cheng.dij.runner;

import java.io.IOException;

/**
 * JDIDebuggee
 */
public class JDIDebuggee {
    public static void main(String[] args) throws IOException {
//        String jpda = "Java Platform Debugger Architecture";
//        System.out.println("Hi, Welcome to " + jpda);
//
//        String jdi = "Java Debug Interface";
//        System.out.println("Today, we'll dive into " + jdi);
//
//        JDIDebugger debuggerInstance = new JDIDebugger();
//        debuggerInstance.setDebugClass(JDIDebuggee.class);
//
//        String[] classPaths = { "target/classes" };
//        debuggerInstance.setClassPaths(classPaths);
//
//        int[] breakPoints = { 21, 24 };
//        debuggerInstance.setBreakPointLines(breakPoints);
//
//        VirtualMachine vm = null;
//        try {
//            vm = debuggerInstance.connectAndLaunchVM();
//            debuggerInstance.enableClassPrepareRequest(vm);
//            EventSet eventSet;
//            while ((eventSet = vm.eventQueue().remove()) != null) {
//                for (Event event : eventSet) {
//                    if (event instanceof VMStartEvent) {
//                        System.out.println("Virtual Machine started");
//                    } else if (event instanceof VMDeathEvent) {
//                        System.out.println("Virtual Machine is dead");
//                    } else if (event instanceof VMDisconnectEvent) {
//                        System.out.println("Virtual Machine is disconnected");
//                        return;
//                    } else if (event instanceof ClassPrepareEvent) {
//                         Set breakpoints while preparing the class.
//                        debuggerInstance.setBreakPoints(vm, (ClassPrepareEvent) event);
//                    } else if (event instanceof BreakpointEvent) {
//                        debuggerInstance.displayVariables((BreakpointEvent) event);
//                        debuggerInstance.enableStepRequest(vm, (BreakpointEvent) event);
//                    } else if (event instanceof StepEvent) {
//                        debuggerInstance.displayVariables((StepEvent) event);
//                    } else {
//                        System.out.println("Unrecognized event: " + event);
//                    }
//                    vm.resume();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (vm != null) {
//                InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
//                OutputStreamWriter writer = new OutputStreamWriter(System.out);
//                char[] buf = new char[512];
//                reader.read(buf);
//                writer.write(buf);
//                writer.flush();
//            }
//        }
    }
}