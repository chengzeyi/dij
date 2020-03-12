package pers.cheng.dij.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;

import java.util.List;

public class DebugSession implements IDebugSession {
    private VirtualMachine vm;
    private EventHub eventHub = new EventHub();

    public DebugSession(VirtualMachine virtualMachine) {
        vm = virtualMachine;
    }

    @Override
    public void start() {
        EventRequest threadStartRequest = vm.eventRequestManager().createThreadStartRequest();
        threadStartRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        threadStartRequest.enable();

        EventRequest threadDeathRequest = vm.eventRequestManager().createThreadDeathRequest();
        threadDeathRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        threadDeathRequest.enable();

        eventHub.start();
    }

    @Override
    public void suspend() {
        vm.suspend();
    }

    @Override
    public void resume() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void terminate() {

    }

    @Override
    public IBreakpoint createBreakpoint(String className, int lineNumber, int hitCount, String condition, String logMessage) {
        return null;
    }

    @Override
    public void setExceptionBreakpoints(boolean notifyCaught, boolean notifyUncaught) {

    }

    @Override
    public Process getProcess() {
        return null;
    }

    @Override
    public List<ThreadReference> getAllThreads() {
        return null;
    }

    @Override
    public IEventHub getEventHub() {
        return null;
    }

    @Override
    public VirtualMachine getVM() {
        return null;
    }
}
