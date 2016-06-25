package com.db.iss.trade.api.registry;

import java.net.URL;

/**
 * Created by adny on 16/6/25.
 * @author andy.shif
 * 注册节点信息
 */
public class RegistryNode {

    //节点名称
    private String node;
    //节点URL
    private URL url;


    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
