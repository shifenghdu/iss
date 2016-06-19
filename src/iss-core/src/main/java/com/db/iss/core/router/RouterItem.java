package com.db.iss.core.router;

public class RouterItem {

    // 正则表达式
    private String systemid;
    private String fuctionid;
    private String tag;
    // 节点名
    private String node;
    // 插件名
    private String plugin;
    // 是否截断路由
    private String truncate;

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getTruncate() {
        return truncate;
    }

    public void setTruncate(String truncate) {
        this.truncate = truncate;
    }

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getFuctionid() {
        return fuctionid;
    }

    public void setFuctionid(String fuctionid) {
        this.fuctionid = fuctionid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
