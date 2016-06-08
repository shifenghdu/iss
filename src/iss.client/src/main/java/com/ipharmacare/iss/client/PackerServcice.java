package com.ipharmacare.iss.client;

import com.ipharmacare.iss.common.esb.EsbMsg;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by andy on 2015/12/26.
 */
public class PackerServcice {
//    private MessagePack messagePack = new MessagePack();
    private ThreadLocal<MessagePack> msgpack = new ThreadLocal<>();

    private static PackerServcice servcice =  new PackerServcice();

    public  static PackerServcice getInstance(){
        return servcice;
    }

    private MessagePack getMsgpack(){
        MessagePack m = msgpack.get();
        if(m == null){
            synchronized (msgpack) {
                m = new MessagePack();
                msgpack.set(m);
            }
        }
        return m;
    }

    public byte[] pack(Object o) throws IOException {
        return  getMsgpack().write(o);
    }

    public Object unpack(byte[] bytes,Class<?> classz) throws IOException {
        return getMsgpack().read(bytes,classz);
    }

}
