package com.db.iss.dispatcher;

import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.dispatcher.future.IFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 返回结果映射
 */
public class ResponseMapper {

    private static ResponseMapper instance = new ResponseMapper();

    //结果等待映射
    private Map<String,IFuture<EsbMsg>> mapper = new ConcurrentHashMap<>();


    public static ResponseMapper getInstance(){
        return instance;
    }

    public void put(String namespace,String method,IFuture<EsbMsg> future){
        mapper.put(genkey(namespace,method,Thread.currentThread().getId()),future);
    }

    public IFuture<EsbMsg> get(String namespace,String method,Long packId){
        return mapper.get(genkey(namespace,method,packId));
    }

    private String genkey(String namespace,String method,Long packId){
        return String.format("%s#%s#%d",namespace,method,packId);
    }

}
