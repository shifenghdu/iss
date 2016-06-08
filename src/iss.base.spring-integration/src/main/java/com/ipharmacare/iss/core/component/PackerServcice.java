package com.ipharmacare.iss.core.component;

import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by andy on 2015/12/26.
 */
public class PackerServcice {
    private ThreadLocal<MessagePack> messagePack = new ThreadLocal<>();

    private static PackerServcice servcice =  new PackerServcice();

    public static PackerServcice getInstance(){
        return servcice;
    }

    private MessagePack getMessagePack(){
        MessagePack m = messagePack.get();
        if(m == null){
            synchronized (messagePack) {
                m = new MessagePack();
                messagePack.set(m);
            }
        }
        return m;
    }

    public byte[] pack(Object o) throws IOException {
        return  getMessagePack().write(o);
    }

    public Object unpack(byte[] bytes,Class<?> classz) throws IOException {
        return getMessagePack().read(bytes,classz);
    }

}
