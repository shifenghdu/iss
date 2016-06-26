package com.db.iss.dispatcher.future;

import com.db.iss.core.exception.TimeoutException;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 异步Future Result
 */
public interface IFuture<T> {

    /**
     * 同步获取结果
     * @param timeout
     * @return
     */
    T get(long timeout) throws TimeoutException;


    /**
     * 设置结果并唤醒等待线程
     * @param result
     */
    void set(T result);

}
