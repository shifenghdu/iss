package com.db.iss.dispatcher.spring;

import com.db.iss.dispatcher.future.IFuture;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 远程服务provider接口
 */
public interface IRemoteServiceProvider {

    /**
     * 获取远程接口同步实现
     * @param type
     * @param <T>
     * @return
     */
    <T> T getService(Class<T> type);

}
