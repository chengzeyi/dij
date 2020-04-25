package pers.cheng.dij.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.DijSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DebugSession implements IDebugSession {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private final VirtualMachine vm;
    private final EventHub eventHub = new EventHub();

    DebugSession(VirtualMachine virtualMachine) {
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

        eventHub.start(vm);
        LOGGER.info("The debug session has successfully started");
    }

    @Override
    public void waitFor() {
        int timeout = DijSettings.getCurrent().getTimeout();
        try {
            if (timeout <= 0) {
                vm.process().waitFor();
            } else {
                vm.process().wait((long) timeout * 1000L * 1000L);
            }
        } catch (InterruptedException e) {
            LOGGER.warning(String.format("The current thread is interrupted, %s", e));
        }
        LOGGER.info("Stopped waiting for the target thread");
    }

    @Override
    public void suspend() {
        vm.suspend();
    }

    @Override
    public void resume() {
        for (ThreadReference tr : DebugUtility.getAllThreadsSafely(this)) {
            while (!tr.isCollected() && tr.suspendCount() > 1) {
                tr.resume();
            }
        }
        vm.resume();
    }

    @Override
    public void detach() {
        vm.dispose();
    }

    @Override
    public void terminate() {
        if (vm.process() == null || vm.process().isAlive()) {
            vm.exit(0);
        }
    }

    @Override
    public IBreakpoint createBreakpoint(String className, int lineNumber, int hitCount, String condition,
            String logMessage) {
        return new EvaluatableBreakpoint(vm, getEventHub(), className, lineNumber, hitCount, condition, logMessage);
    }

    @Override
    public IBreakpoint createBreakpoint(String className, int lineNumber) {
        return new EvaluatableBreakpoint(vm, getEventHub(), className, lineNumber);
    }

    @Override
    public void setExceptionBreakpoints(boolean notifyCaught, boolean notifyUncaught) {
        EventRequestManager manager = vm.eventRequestManager();
        // Create a new list since the original list is unmodifiable.
        ArrayList<ExceptionRequest> legacy = new ArrayList<>(manager.exceptionRequests());
        // Remove all exception requests.
        manager.deleteEventRequests(legacy);
        // When no exception breakpoints are requests are requests, no need to
        // create an empty exception request.
        if (notifyCaught || notifyUncaught) {
            // java-debug says that if this method is not called,
            // there will be a bug.
            vm.allThreads();
            ExceptionRequest request = manager.createExceptionRequest(null, notifyCaught, notifyUncaught);
            request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            request.enable();
            LOGGER.info(String.format("Exception request enabled, notifyCaught: %b, notifyUncaught: %b", notifyCaught, notifyUncaught));
        }
    }

    @Override
    public Process getProcess() {
        return vm.process();
    }

    @Override
    public List<ThreadReference> getAllThreads() {
        return vm.allThreads();
    }

    @Override
    public IEventHub getEventHub() {
        return eventHub;
    }

    @Override
    public VirtualMachine getVM() {
        return vm;
    }
}
