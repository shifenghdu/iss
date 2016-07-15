package com.db.iss.core.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化provider
 * @author andy.shif
 */
public class SerializerProvider {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 实现注册
     */
	private Map<String,String> className = new ConcurrentHashMap<String, String>(){{
		put(SerializerType.MSGPACK.getValue(),"com.db.iss.core.serializer.msgpack.MessagePackSerializerAdapter");
		put(SerializerType.JSON.getValue(),"com.db.iss.core.serializer.json.FastJsonSerializerAdapter");
	}};

    /**
     * 序列化类型
     */
    private String type = SerializerType.JSON.getValue();


    /**
     * 获取序列化对象
     * @return
     */
	public ISerializer getSerializer(){
		try {
			return (ISerializer) Class.forName(className.get(type)).newInstance();
		} catch (Throwable e) {
			logger.error("get serializer failed",e);
		}
		return null;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
