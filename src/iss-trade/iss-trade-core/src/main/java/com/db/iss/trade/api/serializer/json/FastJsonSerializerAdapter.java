package com.db.iss.trade.api.serializer.json;

import com.alibaba.fastjson.JSON;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.serializer.ISerializer;


/**
 * Created by andy on 16/6/22.
 * @author andy.shif
 * fastjson实现
 */
public class FastJsonSerializerAdapter implements ISerializer {

    @Override
    public byte[] encode(Object object) throws Exception {
        return JSON.toJSONBytes(object);
    }

    @Override
    public Object decode(byte[] bytes, Class type) throws Exception {
        return JSON.parseObject(bytes,type);
    }

}
