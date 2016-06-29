package com.db.iss.core.cm;

/**
 * Created by andy on 16/6/26.
 * @author andy.shif
 * 配置key
 */
public enum SettingKey {

    SERIALIZER("iss.serializer"),   // msgpack,json

    COMPRESSOR("iss.compressor"),   // lz4

    REGISTRY("iss.registry"), // zookeeper://127.0.0.1:2181,zookeeper://127.0.0.1:2182

    NODE("iss.node"), //节点名称

    LISTEN("iss.listen"), //服务监听端口

    LOGSWICTH("iss.logger.switch"), //logger插件记录日志开关

    PIPE("iss.pipe"); // 消息处理链  cluster | dispatcher




    private String value;
    SettingKey(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
