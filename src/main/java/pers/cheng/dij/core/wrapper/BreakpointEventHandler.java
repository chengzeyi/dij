package pers.cheng.dij.core.wrapper;

import java.util.logging.Logger;

import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugEvent;
import pers.cheng.dij.core.DebugException;

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
            try {
                handleBreakpointEvent(breakpointEvent);
                successful = true;
            } catch (DebugException e) {
                LOGGER.severe(String.format("Failed to handle breakpointEvent, %s", e));
            }
        }, onError -> {
            LOGGER.severe(onError.toString());
            onError.printStackTrace();
        });
    }

    protected abstract void handleBreakpointEvent(BreakpointEvent breakpointEvent) throws DebugException;

    public boolean isBreakpointHitten() {
        return breakpointHitten;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
