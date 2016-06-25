package com.db.iss.trade.api.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {

//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<SerializerType,String> className = new ConcurrentHashMap<SerializerType,String>(){{
		put(SerializerType.MSGPACK,"com.db.iss.trade.api.serializer.msgpack.MessagePackSerializerAdapter");
		put(SerializerType.JSON,"com.db.iss.trade.api.serializer.json.FastJsonSerializerAdapter");
	}};


	public synchronized ISerializer getSerializer(SerializerType type){
		try {
			return (ISerializer) Class.forName(className.get(type)).newInstance();
		} catch (Throwable e) {
//			logger.error("get serializer failed",e);
		}
		return null;
	}

}
