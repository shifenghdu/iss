package com.db.iss.logger;

import com.alibaba.fastjson.JSON;
import com.db.iss.core.cm.SettingKey;
import com.db.iss.core.plugin.AbstractMessagePlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.plugin.PluginException;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 16/6/29.
 * @author andy.shif
 * 流水线日志记录插件
 */
public class LoggerPlugin extends AbstractMessagePlugin {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int sw = 0; // 0 - off  1 - on

    public LoggerPlugin() {
        super("logger", "v1.0.0", ThreadMode.SHARED);
    }

    @Override
    protected void onStart() throws PluginException {
        Optional<String> switchFlag = Optional.fromNullable(configManager.getSettingValue(SettingKey.LOGSWICTH.getValue()));
        if(switchFlag.or("").equalsIgnoreCase("on")){
            sw = 1;
        }else if(switchFlag.or("").equalsIgnoreCase("off")){
            sw = 0;
        }
    }

    @Override
    protected void onStop() throws PluginException {

    }

    @Override
    protected EsbMsg onForward(EsbMsg message) throws PluginException {
        if(sw == 1){
            logger.warn("forward {}", JSON.toJSONString(message));
        }
        return message;
    }

    @Override
    protected EsbMsg onBackward(EsbMsg message) throws PluginException {
        if(sw == 1){
            logger.warn("backward {}", JSON.toJSONString(message));
        }
        return message;
    }
}
