package pers.cheng.dij.core.wrapper;

import java.util.List;
import java.util.logging.Logger;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.BreakpointEvent;
import pers.cheng.dij.Configuration;

public class FirstBreakpointEventHandler extends BreakpointEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

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
            LOGGER.severe(String.format("Cannot get stack frames from the target thread, %s", e));
            return false;
        }

        StackFrame topStackFrame = stackFrames.get(0);
        LOGGER.info("Got top stack frame");
        breakpointContext = new BreakpointContext();
        return breakpointContext.processTopStackFrame(topStackFrame);
    }
}
