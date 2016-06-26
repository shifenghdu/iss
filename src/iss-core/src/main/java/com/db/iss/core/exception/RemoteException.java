package com.db.iss.core.exception;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 远程服务异常
 */
public class RemoteException extends RuntimeException {

    public RemoteException() {

    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
