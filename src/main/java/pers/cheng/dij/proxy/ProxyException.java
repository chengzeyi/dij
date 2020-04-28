package pers.cheng.dij.proxy;

public class ProxyException extends Exception {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private boolean userError = false;

    public ProxyException() {
        super();
    }

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }

    public ProxyException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProxyException(String message, int errorCode, boolean userError) {
        super(message);
        this.errorCode = errorCode;
        this.userError = userError;
    }

    public ProxyException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ProxyException(Throwable cause, int errorCode) {
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
