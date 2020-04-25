package pers.cheng.dij.core.wrapper.handler;

import java.util.Collections;
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
import pers.cheng.dij.DijSettings;
import pers.cheng.dij.core.DebugEvent;
import pers.cheng.dij.core.wrapper.CrashInformation;

public class ExceptionEventHandler {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);
    
    private CrashInformation crashInformation = null;

    private boolean successfulReproduction = false;

    public ExceptionEventHandler(CrashInformation crashInformation) {
        this.crashInformation = crashInformation;
    }

    public void setExceptionEvents(Observable<DebugEvent> exceptionEvents) {
        successfulReproduction = false;

        exceptionEvents.subscribe(debugEvent -> {
            ExceptionEvent exceptionEvent = (ExceptionEvent) debugEvent.getEvent();
            handleExceptionEvent(exceptionEvent);
        }, onError -> {
            if (onError instanceof RuntimeException) {
                LOGGER.severe(onError.toString());
                onError.printStackTrace();
            }
        });
    }

    private void handleExceptionEvent(ExceptionEvent exceptionEvent) {
        if (!successfulReproduction) {
            successfulReproduction = evaluateReproduction(exceptionEvent);
            return;
        }
        LOGGER.info("Previous reproduction was successful, no need to handle this exceptionEvent");
    }

    public boolean isSuccessful() {
        return successfulReproduction;
    }

    private boolean evaluateReproduction(ExceptionEvent exceptionEvent) {
        ObjectReference exceptionReference = exceptionEvent.exception();
        Method toStringMethod = null;
        for (Method method : exceptionReference.referenceType().allMethods()) {
            if ("toString".equals(method.name()) && "()Ljava/lang/String;".equals(method.signature())) {
                toStringMethod = method;
                LOGGER.info(String.format("toString() method of referenceType %s is found", exceptionReference.referenceType()));
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
                    .format("Failed to get the return value of method Exception.toString(): %s", e));
            return false;
        }

        LOGGER.info(String.format("Got exceptionString: %s", exceptionString));

        String exceptionLine = crashInformation.getExceptionLine();
        
        int editDistance = DijSettings.getCurrent().getEditDistance();
        if (editDistance <= 0) {
            return exceptionString.trim().equals(exceptionLine);
        }
        return calcEditDistance(exceptionString, exceptionLine) <= editDistance;
    }

    private static int calcEditDistance(String exceptionA, String exceptionB) {
        int lenA = exceptionA.length();
        int lenB = exceptionB.length();

        int[][] dp = new int[lenA + 1][lenB + 1];

        for (int i = 0; i <= lenA; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= lenB; j++) {
            dp[0][j] = j;
        }

        for (int i = 0; i < lenA; i++) {
            char c1 = exceptionA.charAt(i);
            for (int j = 0; j < lenB; j++) {
                char c2 = exceptionB.charAt(j);

                if (c1 == c2) {
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[lenA][lenB];
    }
}
