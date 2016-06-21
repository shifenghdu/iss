package com.db.iss.trade.cluster.mina.codec;

/**
 * Created by andy on 16/6/21.
 * @author andy.shif
 * 序列化接口
 */
public interface ISerializer<T> {

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] encode(T object) throws Exception;

    /**
     * 反序列化
     * @param bytes
     * @return
     */
    T decode(byte[] bytes,Class<T> type) throws Exception;

}
