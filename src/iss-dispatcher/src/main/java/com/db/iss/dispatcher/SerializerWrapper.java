package com.db.iss.dispatcher;

import com.db.iss.core.serializer.ISerializer;
import com.db.iss.core.serializer.SerializerFactory;
import com.db.iss.core.serializer.SerializerType;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 序列化操作封装
 */
public class SerializerWrapper {

    private SerializerType serializerType = SerializerType.JSON;

    private SerializerFactory serializerFactory = new SerializerFactory();

    private ThreadLocal<ISerializer> serializers = new ThreadLocal<>();

    private static SerializerWrapper instance = new SerializerWrapper();


    public static SerializerWrapper getInstance(){
        return instance;
    }

    public void setSerializerType(SerializerType type){
        this.serializerType = type;
    }

    public ISerializer getSerializer(){
        ISerializer serializer = serializers.get();
        if(serializer == null){
            serializer = serializerFactory.getSerializer(serializerType);
            serializers.set(serializer);
        }
        return serializer;
    }
}
