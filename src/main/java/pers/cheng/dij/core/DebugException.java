package pers.cheng.dij.core;

public class DebugException extends Exception {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private boolean userError = false;

    public DebugException() {
        super();
    }

    public DebugException(String message) {
        super(message);
    }

    public DebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugException(Throwable cause) {
        super(cause);
    }

    public DebugException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DebugException(String message, int errorCode, boolean userError) {
        super(message);
        this.errorCode = errorCode;
        this.userError = userError;
    }

    public DebugException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DebugException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean isUserError() {
        return userError;
    }

    public void setUserError(boolean userError) {
        this.userError = userError;
    }
}