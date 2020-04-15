package pers.cheng.dij.core.wrapper;

import com.sun.jdi.event.BreakpointEvent;

import io.reactivex.Observable;
import pers.cheng.dij.core.DebugEvent;

public abstract class BreakpointEventHandler {
    private boolean breakpointHitten;
    private boolean successful;

    public void setBreakpointEvents(Observable<DebugEvent> breakpointEvents) {
        breakpointHitten = false;
        successful = false;

        breakpointEvents.subscribe(debugEvent -> {
            if (breakpointHitten) {
                return;
            }
            breakpointHitten = true;
            BreakpointEvent breakpointEvent = (BreakpointEvent) debugEvent.getEvent();
            successful = handleBreakpointEvent(breakpointEvent);
        });
    }

    protected abstract boolean handleBreakpointEvent(BreakpointEvent breakpointEvent);

    public boolean isBreakpointHitten() {
        return breakpointHitten;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
