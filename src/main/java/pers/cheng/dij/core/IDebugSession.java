package pers.cheng.dij.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

import java.util.List;

public interface IDebugSession {
    void start();

    void waitFor();

    void suspend();

    void resume();

    void detach();

    void terminate();

    IBreakpoint createBreakpoint(String className, int lineNumber, int hitCount, String condition, String logMessage);

    IBreakpoint createBreakpoint(String className, int lineNumber);

    void setExceptionBreakpoints(boolean notifyCaught, boolean notifyUncaught);

    Process getProcess();

    List<ThreadReference> getAllThreads();

    IEventHub getEventHub();

    VirtualMachine getVM();
}
