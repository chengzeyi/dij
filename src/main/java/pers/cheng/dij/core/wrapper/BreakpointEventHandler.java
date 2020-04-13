package pers.cheng.dij.core.wrapper;

import java.util.List;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.core.DebugEvent;

public class BreakpointEventHandler {
    private BreakpointContext breakpointContext = null;

    private BpHandlerStatus status = BpHandlerStatus.CONTEXT_UNBINDED;

    private boolean breakpointHitten = false;

    BreakpointEventHandler() {
        status = BpHandlerStatus.CONTEXT_UNBINDED;
    }

    public BpHandlerStatus getStatus() {
        return status;
    }

    public void setBreakpointEvents(Observable<DebugEvent> breakpointEvents) {
        breakpointHitten = false;

        if (status == BpHandlerStatus.UNINITIALIZED) {
            throw new UnsupportedOperationException("This handler has not been initialized.");
        }
        breakpointEvents.subscribe(debugEvent -> {
            BreakpointEvent breakpointEvent = (BreakpointEvent) debugEvent.getEvent();
            handleBreakpointEvent(breakpointEvent);
        });
    }

    private void handleBreakpointEvent(BreakpointEvent breakpointEvent) {
        if (breakpointHitten) {
            return;
        }
        breakpointHitten = true;

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
        List<StackFrame> stackFrames;
        try {
            stackFrames = threadReference.frames();
        } catch (IncompatibleThreadStateException e) {
            status = BpHandlerStatus.CONTEXT_BIND_FAILED;
            return null;
        }
        BreakpointContext breakpointContext = new BreakpointContext();
        breakpointContext.processTopStackFrame(stackFrames.get(0));
        return breakpointContext;
    }
}
