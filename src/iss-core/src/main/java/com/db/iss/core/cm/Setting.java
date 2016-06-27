package com.db.iss.core.cm;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 所有插件配置项
 */
public class Setting {

    private Map<Object,Object> properties = new ConcurrentHashMap<>();

    //配置版本号
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setProperties(Properties properties){
        this.properties.putAll(properties);
    }

    public String getProperty(String key){
        return (String)properties.get(key);
    }

    public void setProperty(String key,String value){
        properties.put(key,value);
    }
}
