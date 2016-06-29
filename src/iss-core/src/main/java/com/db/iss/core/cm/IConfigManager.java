package com.db.iss.core.cm;

/**
 * Created by andy on 16/6/29.
 * 配置中心接口
 * @author andy.shif
 *
 */
public interface IConfigManager {


    /**
     * 通过key值获取配置value
     * @param key
     * @return
     */
    String getSettingValue(String key);


    /**
     * 通过key注册配置数据变更回调
     * @param key
     */
    void subscribeSettingChange(String key,ISettingChangeHandler handler);


}
