package pers.cheng.dij.core.wrapper;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.ExceptionEvent;

import io.reactivex.Observable;
import pers.cheng.dij.Configuration;
import pers.cheng.dij.core.DebugEvent;

public class ExceptionEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    
    private CrashInformation crashInformation = null;

    private boolean reproductionSuccessful = false;

    public ExceptionEventHandler(CrashInformation crashInformation) {
        this.crashInformation = crashInformation;
    }

    public void setExceptionEvents(Observable<DebugEvent> exceptionEvents) {
        reproductionSuccessful = false;

        exceptionEvents.subscribe(debugEvent -> {
            ExceptionEvent exceptionEvent = (ExceptionEvent) debugEvent.getEvent();
            handleExceptionEvent(exceptionEvent);
        }, onError -> {});
    }

    private void handleExceptionEvent(ExceptionEvent exceptionEvent) {
        if (!reproductionSuccessful) {
            reproductionSuccessful = isSuccessfulReproduction(exceptionEvent);
        }
    }

    public boolean isReproductionSuccessful() {
        return reproductionSuccessful;
    }

    private boolean isSuccessfulReproduction(ExceptionEvent exceptionEvent) {
        ObjectReference exceptionReference = exceptionEvent.exception();
        Method toStringMethod = null;
        for (Method method : exceptionReference.referenceType().allMethods()) {
            if ("toString".equals(method.name()) && "()Ljava/lang/String;".equals(method.signature())) {
                toStringMethod = method;
                break;
            }
        }
        if (toStringMethod == null) {
            LOGGER.severe(String
                    .format("Failed to get toString() method from the reference type %s",
                        exceptionReference.referenceType().name()));
            return false;
        }

        String exceptionString;
        ThreadReference thread = exceptionEvent.thread();
        try {
            Value returnValue = exceptionReference.invokeMethod(thread, toStringMethod, Collections.emptyList(),
                    ObjectReference.INVOKE_SINGLE_THREADED);
            exceptionString = ((StringReference) returnValue).value();
        } catch (InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException
                | InvocationException e) {
            LOGGER.severe(String
                    .format("Failed to get the return value of the method Exception.toString(): %s", e));
            return false;
        }

        LOGGER.info(String.format("Got exceptionString: %s", exceptionString));

        String exceptionLine = crashInformation.getExceptionLine();
        return exceptionString.trim().equals(exceptionLine);
    }
}
