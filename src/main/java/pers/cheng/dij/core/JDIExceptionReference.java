package pers.cheng.dij.core;

import com.sun.jdi.ObjectReference;

public class JDIExceptionReference {
    public ObjectReference exception;

    public boolean isUncaught;

    public JDIExceptionReference(ObjectReference exception, boolean isUncaught) {
        this.exception = exception;
        this.isUncaught = isUncaught;
    }
}
