package pers.cheng.dij;

public class DijException extends Exception {
    private static final long serialVersionUID = 1L;
    private int errorCode;
    private boolean userError = false;

    public DijException() {
        super();
    }

    public DijException(String message) {
        super(message);
    }

    public DijException(String message, Throwable cause) {
        super(message, cause);
    }

    public DijException(Throwable cause) {
        super(cause);
    }

    public DijException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DijException(String message, int errorCode, boolean userError) {
        super(message);
        this.errorCode = errorCode;
        this.userError = userError;
    }

    public DijException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DijException(Throwable cause, int errorCode) {
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
