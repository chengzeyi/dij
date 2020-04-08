package pers.cheng.dij.core;

import com.sun.jdi.request.EventRequest;
import io.reactivex.disposables.Disposable;

import java.util.List;

public interface IDebugResource extends AutoCloseable {
    List<EventRequest> getRequests();

    List<Disposable> getSubscriptions();
}
