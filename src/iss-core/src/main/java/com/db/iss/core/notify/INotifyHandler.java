package com.db.iss.core.notify;

import java.util.Objects;

/**
 * Created by apple on 16/6/20.
 * @author andy.shif
 * 消息回调接口
 */
public interface INotifyHandler {

    /**
     * 消息回调接口
     * @param message
     */
    void onMessage(byte[] message);

}
