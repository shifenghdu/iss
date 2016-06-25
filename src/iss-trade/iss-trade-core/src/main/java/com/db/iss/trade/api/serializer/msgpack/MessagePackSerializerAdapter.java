package com.db.iss.trade.api.serializer.msgpack;

import com.db.iss.trade.api.plugin.EsbMsg;
import com.db.iss.trade.api.serializer.ISerializer;
import org.msgpack.MessagePack;

/**
 * Created by andy on 16/6/22.
 * @author andy.shi
 * 序列化适配器
 */
public class MessagePackSerializerAdapter implements ISerializer {

    private MessagePack messagePack = new MessagePack();

    @Override
    public byte[] encode(Object object) throws Exception {
        return messagePack.write(object);
    }

    @Override
    public Object decode(byte[] bytes, Class type) throws Exception {
        return messagePack.read(bytes,type);
    }
}
