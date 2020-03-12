package pers.cheng.dij.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

import java.util.List;

public interface IDebugSession {
    public void start();

    public void suspend();

    public void resume();

    public void detach();

    public void terminate();

    public IBreakpoint createBreakpoint(String className, int lineNumber, int hitCount, String condition, String logMessage);

    public void setExceptionBreakpoints(boolean notifyCaught, boolean notifyUncaught);

    public Process getProcess();

    public List<ThreadReference> getAllThreads();

    public IEventHub getEventHub();

    public VirtualMachine getVM();
}
