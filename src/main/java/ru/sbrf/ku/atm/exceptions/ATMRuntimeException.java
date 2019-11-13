package ru.sbrf.ku.atm.exceptions;

public class ATMRuntimeException extends RuntimeException {
    public ATMRuntimeException() {
    }

    public ATMRuntimeException(String message) {
        super(message);
    }

    public ATMRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ATMRuntimeException(Throwable cause) {
        super(cause);
    }

    public ATMRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
