package com.db.iss.core.notify;

/**
 * Created by andy on 16/6/27.
 * @author andy.shif
 * notify工厂
 */
public interface INotifyFactory {

    /**
     * 获取notify实现
     * @return
     */
    INotify getNotify();

}
