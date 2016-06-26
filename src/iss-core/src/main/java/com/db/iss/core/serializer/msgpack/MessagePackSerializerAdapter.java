package com.db.iss.core.serializer.msgpack;

import com.db.iss.core.serializer.ISerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

/**
 * Created by andy on 16/6/22.
 * @author andy.shi
 * msgpack v0.8序列化适配器
 */
public class MessagePackSerializerAdapter implements ISerializer {

    private ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    @Override
    public byte[] encode(Object object) throws Exception {
        return objectMapper.writeValueAsBytes(object);
    }

    @Override
    public Object decode(byte[] bytes, Class type) throws Exception {
        return objectMapper.readValue(bytes,type);
    }
}
