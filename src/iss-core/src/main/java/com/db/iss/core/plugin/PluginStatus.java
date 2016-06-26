package com.db.iss.core.plugin;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 插件状态
 */
public enum PluginStatus {

    CLOSE(0,"关闭"),OPEN(1,"启动");

    private Integer value;
    private String describe;

    PluginStatus(Integer value,String describe){
        this.value = value;
        this.describe = describe;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescribe() {
        return describe;
    }
}
