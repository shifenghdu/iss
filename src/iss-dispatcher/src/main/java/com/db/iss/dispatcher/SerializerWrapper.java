package com.db.iss.dispatcher;

import com.db.iss.core.serializer.ISerializer;
import com.db.iss.core.serializer.SerializerProvider;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 序列化操作封装
 */
public class SerializerWrapper {

    private SerializerProvider serializerProvider;

    private ThreadLocal<ISerializer> serializers = new ThreadLocal<>();

    public SerializerWrapper(SerializerProvider provider){
        serializerProvider = provider;
    }

    public ISerializer getSerializer(){
        ISerializer serializer = serializers.get();
        if(serializer == null){
            serializer = serializerProvider.getSerializer();
            serializers.set(serializer);
        }
        return serializer;
    }
}
