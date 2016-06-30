package com.db.iss.core.serializer.json;

import com.alibaba.fastjson.JSON;
import com.db.iss.core.serializer.ISerializer;


/**
 * Created by andy on 16/6/22.
 * @author andy.shif
 * fastjson实现
 */
public class FastJsonSerializerAdapter implements ISerializer {

    private final String CODEC =  "UTF-8";

    @Override
    public byte[] encode(Object object) throws Exception {
        return JSON.toJSONString(object).getBytes(CODEC);
    }

    @Override
    public Object decode(byte[] bytes, Class type) throws Exception {
        return JSON.parseObject(new String(bytes,CODEC),type);
    }

}
