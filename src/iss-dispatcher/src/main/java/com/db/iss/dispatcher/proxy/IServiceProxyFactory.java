package com.db.iss.dispatcher.proxy;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 服务实现代理工厂
 */
public interface IServiceProxyFactory {

    /**
     * 获取服务实现代理
     * @param inter
     * @param <T>
     * @return
     */
    <T> T getService(Class<T> inter);

}
