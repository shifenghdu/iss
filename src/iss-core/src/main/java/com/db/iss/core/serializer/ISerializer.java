package com.db.iss.core.serializer;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 序列化接口
 */
public interface ISerializer {

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] encode(Object object) throws Exception;

    /**
     * 反序列化
     * @param bytes
     * @return
     */
    Object decode(byte[] bytes,Class type) throws Exception;

}
