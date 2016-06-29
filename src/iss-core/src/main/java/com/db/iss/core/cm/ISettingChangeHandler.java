package com.db.iss.core.cm;

/**
 * Created by andy on 16/6/29.
 * @author andy.shif
 * 配置变更回调
 */
public interface ISettingChangeHandler {

    /**
     * 配置变更回调接口
     * @param key
     * @param value
     */
    void onChange(String key, String value);

}
