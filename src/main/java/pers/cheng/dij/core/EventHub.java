package pers.cheng.dij.core;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.*;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import pers.cheng.dij.Configuration;

import java.util.logging.Logger;

public class EventHub implements IEventHub {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private PublishSubject<DebugEvent> subject = PublishSubject.create();

    private Thread workingThread = null;
    private boolean isClosed = false;

    public Observable<DebugEvent> getEvents() {
        return subject;
    }

    @Override
    public Observable<DebugEvent> getBreakpointEvents() {
        return getEvents().filter(debugEvent -> debugEvent.getEvent() instanceof BreakpointEvent);
    }

    @Override
    public Observable<DebugEvent> getThreadEvents() {
        return getEvents().filter(debugEvent -> debugEvent.getEvent() instanceof ThreadStartEvent ||
                 debugEvent.getEvent() instanceof ThreadStartEvent);
    }

    @Override
    public Observable<DebugEvent> getExceptionEvents() {
        return getEvents().filter(debugEvent -> debugEvent.getEvent() instanceof ExceptionEvent);
    }

    @Override
    public Observable<DebugEvent> getStepEvents() {
        return getEvents().filter(debugEvent -> debugEvent.getEvent() instanceof StepEvent);
    }

    @Override
    public Observable<DebugEvent> getVMEvents() {
        return getEvents().filter(debugEvent -> debugEvent.getEvent() instanceof VMStartEvent ||
                debugEvent.getEvent() instanceof VMDisconnectEvent ||
                debugEvent.getEvent() instanceof VMDeathEvent);
    }

    public void start(VirtualMachine vm) {
        if (isClosed) {
            throw new IllegalStateException("This event hub is already closed.");
        }

        workingThread = new Thread(() -> {
            EventQueue queue = vm.eventQueue();
            while (true) {
                try {
                    if (Thread.interrupted()) {
                        subject.onComplete();
                        return;
                    }

                    EventSet set = queue.remove();

                    boolean shouldResume = true;
                    for (Event event : set) {
                        LOGGER.info(String.format("JDI Event: %s", event));
                        DebugEvent debugEvent = new DebugEvent();
                        debugEvent.setEvent(event);
                        debugEvent.setEventSet(set);
                        subject.onNext(debugEvent);
                        shouldResume &= debugEvent.isShouldResume();
                    }

                    if (shouldResume) {
                        set.resume();
                        LOGGER.info("The target VM has been resumed");
                    }
                } catch (InterruptedException e) {
                    isClosed = true;
                    subject.onComplete();
                    LOGGER.warning(String.format("The thread has been interrupted, %s", e));
                    return;
                } catch (VMDisconnectedException e) {
                    isClosed = true;
                    subject.onComplete();
                    // subject.onError(e);
                    LOGGER.warning(String.format("The target VM has been disconnected, %s", e));
                    return;
                }
            }
        }, "EventHub");

        workingThread.start();
        LOGGER.info("The target VM thread has been started");
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        workingThread.interrupt();
        workingThread = null;
        isClosed = true;
    }
}
