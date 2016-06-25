package com.db.iss.trade.api.alarm;

/**
 * Created by andy on 16/6/19.
 * @author andy.shif
 */
public interface IAlarm {

    /**
     * 发送警报
     * @param level
     * @param message
     * @param args
     */
    void sendAlarm(AlarmLevel level,String message, String ... args);


}
