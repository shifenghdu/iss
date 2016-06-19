package com.db.iss.trade.api.exception;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 配置异常
 */
public class SettingException extends ISSException {

    public SettingException() {
    }

    public SettingException(String message) {
        super(message);
    }

    public SettingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SettingException(Throwable cause) {
        super(cause);
    }

    public SettingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
