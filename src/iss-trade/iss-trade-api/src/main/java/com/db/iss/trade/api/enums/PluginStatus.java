package com.db.iss.trade.api.enums;

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

}
