package com.db.iss.trade.api.enums;

/**
 * Created by andy on 16/6/19.
 * @author andy.shif
 * 警告级别
 */
public enum  AlarmLevel {

    NORMAL(0,"警告"),EMERGENCY(2,"紧急"),IMPORTANT(1,"重要");

    private Integer level;
    private String describe;

    AlarmLevel(Integer level,String describe){
        this.level = level;
        this.describe = describe;
    }

    public Integer getLevel() {
        return level;
    }

    public String getDescribe() {
        return describe;
    }
}
