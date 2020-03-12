package pers.cheng.dij.core;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;

public class DebugEvent {
    private Event event = null;

    private EventSet eventSet = null;

    private boolean shouldResume = true;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventSet getEventSet() {
        return eventSet;
    }

    public void setEventSet(EventSet eventSet) {
        this.eventSet = eventSet;
    }

    public boolean isShouldResume() {
        return shouldResume;
    }

    public void setShouldResume(boolean shouldResume) {
        this.shouldResume = shouldResume;
    }
}
