package com.db.iss.core.cm;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shi
 * 配置接口
 */
public interface IConfigurable {

    /**
     * 设置配置参数
     * @param setting
     * @throws SettingException
     */
    void setSetting(Setting setting) throws SettingException;



}
