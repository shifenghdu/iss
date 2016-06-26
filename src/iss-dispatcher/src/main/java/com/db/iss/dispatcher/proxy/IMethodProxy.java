package com.db.iss.dispatcher.proxy;

/**
 * Created by andy on 16/6/25.
 * @author andy.shif
 * 反射代理接口
 */
public interface IMethodProxy {

    /**
     * 反射接口
     * @param args
     * @return
     */
    Object invoke(Object[] args) throws Throwable;


    /**
     * 设置被代理对象 (用于代理工厂设置被代理对象)
     * @param object
     */
    void setTarget(Object object);

}
