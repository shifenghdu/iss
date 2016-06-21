package com.db.iss.trade.cluster.mina.codec;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 序列化类型
 */
public enum  SerializerType {

    MSGPACK(0,"msgpack"),JSON(1,"json");

    private int value;
    private String name;

    SerializerType(int value,String name){
        this.value = value;
        this.name = name;
    }
}
