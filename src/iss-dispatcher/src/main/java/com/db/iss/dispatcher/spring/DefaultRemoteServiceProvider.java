package com.db.iss.dispatcher.spring;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 远程服务provider
 */
@Service
public class DefaultRemoteServiceProvider implements IRemoteServiceProvider{

    private Map<Class,Object> proxyMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getService(Class<T> type) {
        T proxy = (T)proxyMap.get(type);
        if(proxy == null){
            if(proxy == null) {
                proxyMap.put(type, proxy);
            }
        }
        return proxy;
    }

}
