package pers.cheng.dij.wrapper;

import java.util.List;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.core.DebugEvent;

public class BreakpointEventHandler {
    private Observable<DebugEvent> breakpointEvents = null;

    private CrashInformation crashInformation = null;

    private BreakpointContext breakpointContext = null;

    private BpHandlerStatus status = BpHandlerStatus.UNINITIALIZED;

    public BpHandlerStatus getStatus() {
        return status;
    }

    public void setBreakpointEvents(Observable<DebugEvent> breakpointEvents) {
        if (status == BpHandlerStatus.UNINITIALIZED) {
            throw new UnsupportedOperationException("This handler has not been initialized.");
        }
        breakpointEvents.subscribe(debugEvent -> {
            BreakpointEvent breakpointEvent = (BreakpointEvent) debugEvent.getEvent();
            handleBreakpointEvent(breakpointEvent);
        });
    }

    public void setCrashInformation(CrashInformation crashInformation) {
        this.crashInformation = crashInformation;
        status = BpHandlerStatus.CONTEXT_UNBINDED;
    }

    private void handleBreakpointEvent(BreakpointEvent breakpointEvent) {
        if (status == BpHandlerStatus.UNINITIALIZED) {
            throw new UnsupportedOperationException("This handler has not been initialized.");
        }
        if (status == BpHandlerStatus.CONTEXT_UNBINDED) {
            // The first to run the virtual machine, need to get context information.
            breakpointContext = getBreakpointContext(breakpointEvent);
        } else if (status == BpHandlerStatus.CONTEXT_BINDED) {
            // Try different values using context information.
        } else if (status == BpHandlerStatus.CONTEXT_BIND_FAILED) {
            // Failed to get breakpoint context, do nothing.
        } else {
            // Stopped.
            // Just do nothing.
        }
    }

    private BreakpointContext getBreakpointContext(BreakpointEvent breakpointEvent) {
        ThreadReference threadReference = breakpointEvent.thread();
        BreakpointContext breakpointContext = null;
        try {
            List<StackFrame> stackFrames = threadReference.frames();
            breakpointContext = new BreakpointContext();
            breakpointContext.setTopStackFrame(stackFrames.get(0));
        } catch (IncompatibleThreadStateException e) {
            status = BpHandlerStatus.CONTEXT_BIND_FAILED;
        }
        return breakpointContext;
    }
}
