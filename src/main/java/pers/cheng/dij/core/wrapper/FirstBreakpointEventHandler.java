package pers.cheng.dij.core.wrapper;

import java.util.List;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;

public class FirstBreakpointEventHandler extends BreakpointEventHandler {
    private BreakpointContext breakpointContext;

    public BreakpointContext getBreakpointContext() {
        return breakpointContext;
    }

    @Override
    protected boolean handleBreakpointEvent(BreakpointEvent breakpointEvent) {
        ThreadReference threadReference = breakpointEvent.thread();
        List<StackFrame> stackFrames;
        try {
            stackFrames = threadReference.frames();
        } catch (IncompatibleThreadStateException e) {
            // Cannot continue to change the value.
            return false;
        }
        StackFrame topStackFrame = stackFrames.get(0);
        breakpointContext = new BreakpointContext();
        breakpointContext.processTopStackFrame(topStackFrame);
        return true;
    }
}
