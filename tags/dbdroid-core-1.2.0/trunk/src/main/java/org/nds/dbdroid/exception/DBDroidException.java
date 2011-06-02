package org.nds.dbdroid.exception;

public class DBDroidException extends Exception {
    private static final long serialVersionUID = 1L;

    private Throwable nestedException;

    public DBDroidException(String message) {
        super(message);
    }

    public DBDroidException(String message, Throwable nestedException) {
        super(message);
        this.nestedException = nestedException;
    }

    public Throwable getNestedException() {
        return nestedException;
    }

    @Override
    public String getMessage() {
        if (nestedException != null) {
            return super.getMessage() + " Nested exception: " + nestedException.getMessage();
        } else {
            return super.getMessage();
        }
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (nestedException != null) {
            System.err.print("Nested exception: ");
            nestedException.printStackTrace();
        }
    }
}
