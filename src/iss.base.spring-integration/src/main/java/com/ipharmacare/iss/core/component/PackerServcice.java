package com.ipharmacare.iss.core.component;

import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by andy on 2015/12/26.
 */
public class PackerServcice {
    private MessagePack messagePack = new MessagePack();

    private static PackerServcice servcice =  new PackerServcice();

    public  static PackerServcice getInstance(){
        return servcice;
    }

    public byte[] pack(Object o) throws IOException {
        return  messagePack.write(o);
    }

    public Object unpack(byte[] bytes,Class<?> classz) throws IOException {
        return messagePack.read(bytes,classz);
    }

}
