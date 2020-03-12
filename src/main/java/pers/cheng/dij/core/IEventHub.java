package pers.cheng.dij.core;

import com.sun.jdi.VirtualMachine;
import io.reactivex.Observable;

public interface IEventHub extends AutoCloseable {
    void start(VirtualMachine vm);

    Observable<DebugEvent> getEvents();

    Observable<DebugEvent> getBreakpointEvents();

    Observable<DebugEvent> getThreadEvents();

    Observable<DebugEvent> getExceptionEvents();

    Observable<DebugEvent> getStepEvents();

    Observable<DebugEvent> getVMEvents();
}
