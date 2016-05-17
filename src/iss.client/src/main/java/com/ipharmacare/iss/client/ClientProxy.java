package com.ipharmacare.iss.client;

import com.ipharmacare.iss.client.handler.MultiServiceHandler;
import com.ipharmacare.iss.client.handler.ServicesHandler;
import com.ipharmacare.iss.common.dispatch.IBizContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 2015/12/29.
 */
public class ClientProxy {
    private static Map<String, ServicesHandler> map = new ConcurrentHashMap<String, ServicesHandler>();
    private static Map<String, MultiServiceHandler> multimap = new ConcurrentHashMap<String, MultiServiceHandler>();
    private static ThreadLocal<List<Object>> resultThreadLocal = new ThreadLocal<List<Object>>();
    private static ThreadLocal<String> tagThreadLocal = new ThreadLocal<String>();

    public static <T> T getProxy(IBizContext context, Class<?> classz) {
        ServicesHandler handler = map.get(String.format("%d|%d", Thread.currentThread().getId(), classz.hashCode()));
        if (handler == null) {
            handler = new ServicesHandler();
            map.put(String.format("%d|%d", Thread.currentThread().getId(), classz.hashCode()), handler);
        }
        return handler.getProxy(context, classz);
    }


    public static <T> T getMultiProxy(IBizContext context, Class<?> classz) {
        MultiServiceHandler handler = multimap.get(String.format("%d|%d", Thread.currentThread().getId(), classz.hashCode()));
        if (handler == null) {
            handler = new MultiServiceHandler();
            multimap.put(String.format("%d|%d", Thread.currentThread().getId(), classz.hashCode()), handler);
        }
        return handler.getProxy(context, classz);
    }

    public static <T> T getMutiResult() {
        return (T) resultThreadLocal.get();
    }

    public static void setMultiResult(List<Object> result) {
        resultThreadLocal.set(result);
    }

    public static String getUserTag() {
        return tagThreadLocal.get() == null ? "":tagThreadLocal.get();
    }

    public static void setUserTag(String tag) {
        tagThreadLocal.set(tag);
    }
}
