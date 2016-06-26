package com.db.iss.core.serializer;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 序列化类型
 */
public enum  SerializerType {

    MSGPACK("msgpack"),JSON("json");

    private String value;

    SerializerType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
