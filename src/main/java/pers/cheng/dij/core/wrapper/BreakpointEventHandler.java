package pers.cheng.dij.core.wrapper;

import java.util.logging.Logger;

import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugEvent;

public abstract class BreakpointEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private boolean breakpointHitten;
    private boolean successful;

    public void setBreakpointEvents(Observable<DebugEvent> breakpointEvents) {
        breakpointHitten = false;
        successful = false;

        breakpointEvents.subscribe(debugEvent -> {
            if (breakpointHitten) {
                LOGGER.info("Breakpoint has been hitten before");
                return;
            }

            breakpointHitten = true;
            BreakpointEvent breakpointEvent = (BreakpointEvent) debugEvent.getEvent();
            successful = handleBreakpointEvent(breakpointEvent);
        }, onError -> {});
    }

    protected abstract boolean handleBreakpointEvent(BreakpointEvent breakpointEvent);

    public boolean isBreakpointHitten() {
        return breakpointHitten;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
