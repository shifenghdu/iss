package com.db.iss.trade.cluster.mina.codec.msgpack;

import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.cluster.mina.codec.ISerializer;
import org.msgpack.MessagePack;

/**
 * Created by andy on 16/6/22.
 * @author andy.shi
 * 序列化适配器
 */
public class MessagePackSerializerAdapter implements ISerializer<EsbMsg>{

    private MessagePack messagePack = new MessagePack();

    @Override
    public byte[] encode(EsbMsg object) throws Exception {
        return messagePack.write(object);
    }

    @Override
    public EsbMsg decode(byte[] bytes, Class<EsbMsg> type) throws Exception {
        return messagePack.read(bytes,type);
    }
}
