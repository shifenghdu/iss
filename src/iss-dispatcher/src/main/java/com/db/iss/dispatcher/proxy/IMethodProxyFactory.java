package com.db.iss.dispatcher.proxy;

import java.lang.reflect.Method;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 反射代理工厂
 */
public interface IMethodProxyFactory {

    /**
     * 获取方法反射代理
     * @param method 被代理方法名
     * @param target 被代理对象
     * @return
     */
    IMethodProxy getProxy(Method method, Object target) throws Exception;

}
