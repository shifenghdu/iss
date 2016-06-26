package com.db.iss.core.exception;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shi
 * ISS 基础异常
 */
public class ISSException extends Exception {

    public ISSException() {
    }

    public ISSException(String message) {
        super(message);
    }

    public ISSException(String message, Throwable cause) {
        super(message, cause);
    }

    public ISSException(Throwable cause) {
        super(cause);
    }

    public ISSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
