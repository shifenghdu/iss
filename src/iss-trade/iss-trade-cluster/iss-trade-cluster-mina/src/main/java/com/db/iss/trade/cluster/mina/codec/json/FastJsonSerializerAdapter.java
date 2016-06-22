package com.db.iss.trade.cluster.mina.codec.json;

import com.alibaba.fastjson.JSON;
import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.cluster.mina.codec.ISerializer;


/**
 * Created by andy on 16/6/22.
 * @author andy.shif
 * fastjson实现
 */
public class FastJsonSerializerAdapter implements ISerializer<EsbMsg>{

    @Override
    public byte[] encode(EsbMsg object) throws Exception {
        return JSON.toJSONBytes(object);
    }

    @Override
    public EsbMsg decode(byte[] bytes, Class<EsbMsg> type) throws Exception {
        return JSON.parseObject(bytes,type);
    }

}
