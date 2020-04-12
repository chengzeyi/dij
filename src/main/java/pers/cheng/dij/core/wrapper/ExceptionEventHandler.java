package pers.cheng.dij.core.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.jdi.ArrayReference;
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
import pers.cheng.dij.core.Configuration;
import pers.cheng.dij.core.DebugEvent;

public class ExceptionEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    private Observable<DebugEvent> exceptionEvents = null;

    private CrashInformation crashInformation = null;

    ExceptionEventHandler(CrashInformation crashInformation) {
        this.crashInformation = crashInformation;
    }

    public void setExceptionEvents(Observable<DebugEvent> exceptionEvents) {
        exceptionEvents.subscribe(debugEvent -> {
            ExceptionEvent exceptionEvent = (ExceptionEvent) debugEvent.getEvent();
            handleExceptionEvent(exceptionEvent);
        });
    }

    private void handleExceptionEvent(ExceptionEvent exceptionEvent) {

    }

    private boolean isSuccessfulReproduction(ExceptionEvent exceptionEvent) {
        ObjectReference exceptionReference = exceptionEvent.exception();
        Method getStackTraceMethod = null;
        for (Method method : exceptionReference.referenceType().allMethods()) {
            if ("getStackTrace".equals(method.name())) {
                getStackTraceMethod = method;
                break;
            }
        }
        if (getStackTraceMethod == null) {
            return false;
        }

        ThreadReference thread = exceptionEvent.thread();
        Value returnValue;
        try {
            returnValue = exceptionReference.invokeMethod(thread, getStackTraceMethod, Collections.emptyList(),
                    ObjectReference.INVOKE_SINGLE_THREADED);
        } catch (InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException
                | InvocationException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to get the return value of the method Exception.toString(): %s.", e.toString()));
            return false;
        }
        ArrayReference jdiStackTraceElementArray = (ArrayReference) returnValue;
        List<Value> jdiStackTraceElements = jdiStackTraceElementArray.getValues();
        List<String> stackTrace = jdiStackTraceElements.stream().map(jdiStackTraceElementValue -> {
            ObjectReference jdiStackTraceElementReference = (ObjectReference) jdiStackTraceElementValue;
            Method toStringMethod = null;
            for (Method method : jdiStackTraceElementReference.referenceType().allMethods()) {
                if ("toString".equals(method.name())) {
                    toStringMethod = method;
                    break;
                }
            }
            if (toStringMethod == null) {
                return null;
            }
            Value retValue;
            try {
                retValue = jdiStackTraceElementReference.invokeMethod(thread, toStringMethod, Collections.emptyList(),
                        ObjectReference.INVOKE_SINGLE_THREADED);
            } catch (InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException
                    | InvocationException e) {
                LOGGER.log(Level.SEVERE, String.format("Failed to get the return value of the method StackTraceElement.toString(): %s.", e.toString()));
                return null;
            }
            StringReference jdiStackTraceElementToStringReference = (StringReference) retValue;
            String jdiStackTraceElementToString = jdiStackTraceElementToStringReference.value();
            return jdiStackTraceElementToString;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<String> crashLines = crashInformation.getCrashLines();
        //TODO: Compare similarity.
        return false;
    }
}
