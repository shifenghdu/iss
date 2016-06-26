package com.db.iss.dispatcher.spring;

import com.db.iss.dispatcher.future.IFuture;
import com.db.iss.dispatcher.proxy.IServiceProxy;

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

    /**
     * 获取远程接口管理接口实现
     * @param type
     * @return
     */
    IServiceProxy getServiceManager(Class<?> type);

}
