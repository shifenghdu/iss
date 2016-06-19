package com.db.iss.trade.api.exception;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 插件基础异常
 */
public class PluginException extends ISSException {
    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    public PluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
