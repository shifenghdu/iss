package com.db.iss.trade.api.cm;

import java.util.Properties;

/**
 * Created by andy on 2016/6/19.
 * @author andy.shif
 * 所有插件配置项
 */
public class Setting extends Properties{

    //配置版本号
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
