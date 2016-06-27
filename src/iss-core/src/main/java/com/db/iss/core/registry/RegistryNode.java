package com.db.iss.core.registry;

import java.net.URL;
import java.util.List;

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
    //命名空间名称(接口名)
    private List<String> namespaces;


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
