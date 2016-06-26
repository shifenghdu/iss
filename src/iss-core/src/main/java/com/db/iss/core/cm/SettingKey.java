package com.db.iss.core.cm;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 配置key
 */
public enum SettingKey {

    SERIALIZER("iss.serializer"),   // msgpack,json

    COMPRESSOR("iss.compressor"),   // lz4

    PIPE("iss.pipe"); // 消息处理链  cluster | dispatcher



    private String value;
    SettingKey(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
