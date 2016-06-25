package com.db.iss.trade.api.registry;

import java.net.URL;

/**
 * Created by andy on 16/6/23.
 * @author andy.shif
 * 本地注册中心接口
 */
public interface IRegistry {

    /**
     * 注册RPC服务命名空间
     * @param namespace
     * @return
     */
    boolean register(String namespace);

    /**
     * 注销RPC服务命名空间
     * @param namespace
     * @return
     */
    boolean unregister(String namespace);


    /**
     * 根据命名空间获取节点信息
     * @param namespace
     * @return
     */
    RegistryNode getNode(String namespace);

}
