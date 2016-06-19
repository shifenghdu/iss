package com.db.iss.trade.api.plugin;
import java.util.List;
import java.util.Vector;

/**
 * Trade 基础消息
 * @author  andy.shif
 */
public class EsbMsg {

    /**
     * 消息类型
     */
    public static final int MSGTYPE_REQ = 0;
    public static final int MSGTYPE_RESP = 1;
    public static final int MSGTYPE_CLUSTER = 2;

    /**
     * 返回状态
     */
    public static final int ESB_SUCC = 0;
    public static final String ESB_SUCC_MSG = "调用成功";
    public static final int ESB_TRANS_ERR = 1;
    public static final String ESB_TRANS_ERR_MSG = "消息转发错误";
    public static final int ESB_TRANS_TIMEOUT = 2;
    public static final String ESB_TRANS_TIMEOUT_MSG = "消息转发超时";
    public static final int ESB_BIZ_DISPATCH_ERR = 3;
    public static final String ESB_BIZ_DISPATCH_ERR_MSG = "业务调度错误";
    public static final int ESB_BIZ_EXECUTE_ERR = 4;
    public static final String ESB_BIZ_EXECUTE_ERR_MSG = "业务处理错误";

    private String version;
    //返回码
    private int retcode;
    //返回消息
    private String retmsg;
    // 包类型
    private int msgtype;
    // 系统号
    private int systemid;
    // 功能号
    private int functionid;
    // 自定义路由标记
    private String tag;
    // 路由路径
    private List<String> routeinfo = new Vector<String>();
    // 转发下一条节点名
    private String nextnode;
    // 业务消息包
    private List<byte[]> params;
    // 发送者插件名
    private String sendname;
    // 发送者附带参数
    private String sendarg;
    // 转发下一节点session
    private long nextSession = 0;
    //消息ID
    private Long packageid;
    //时间戳
    private List<String> timeticks = new Vector<String>();
    // multi call 情况下对应的返回
    private List<EsbMsg> response = new Vector<EsbMsg>();
    // multi call 情况下被复制分发的数量
    private int copyCount = 1;
    // multi call 复制分发标志
    private boolean isCopySend = false;
    //content字段是否压缩
    private boolean isCompress = false;
    //压缩前长度
    private Integer originLen;
    //压缩算法
    private String compressAlgorithm;

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public int getFunctionid() {
        return functionid;
    }

    public void setFunctionid(int functionid) {
        this.functionid = functionid;
    }

    public List<String> getRouteinfo() {
        return routeinfo;
    }

    public void setRouteinfo(List<String> routeinfo) {
        synchronized (this.routeinfo) {
            this.routeinfo = routeinfo;
        }
    }

    public String popLastRouteInfo() {
        synchronized (routeinfo) {
            if (routeinfo.size() >= 1) {
                String info = routeinfo.get(routeinfo.size() - 1);
                routeinfo.remove(routeinfo.size() - 1);
                return info;
            }
            return null;
        }
    }

    public synchronized void appendLastRouteInfo(String s) {
        synchronized (routeinfo) {
            if (routeinfo.size() >= 1) {
                String info = routeinfo.get(routeinfo.size() - 1);
                routeinfo.set(routeinfo.size() - 1, String.format("%s;%s", info, s));
            }
        }
    }

    public void pushRouteInfo(String info) {
        synchronized (routeinfo) {
            routeinfo.add(info);
        }
    }

    public String getNextnode() {
        return nextnode;
    }

    public void setNextnode(String nextnode) {
        this.nextnode = nextnode;
    }

    public List<byte[]> getParams() {
        return params;
    }

    public void setParams(List<byte[]> params) {
        this.params = params;
    }

    public void changeToResponse() {
        this.msgtype = MSGTYPE_RESP;
        this.retcode = ESB_SUCC;
        this.retmsg = ESB_SUCC_MSG;
    }

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retMsg) {
        this.retmsg = retMsg;
    }

    public int getSystemid() {
        return systemid;
    }

    public void setSystemid(int systemid) {
        this.systemid = systemid;
    }

    public String getSendname() {
        return sendname;
    }

    public void setSendname(String sendname) {
        this.sendname = sendname;
    }

    public String getSendarg() {
        return sendarg;
    }

    public void setSendarg(String sendarg) {
        this.sendarg = sendarg;
    }

    @Override
    public String toString() {
        return String
                .format("msgtype:[%d] systemid:[%d] functionid:[%d] routerinfo:[%s] sender:[%s|%s] next[%s] session[%d] iscopy[%b] copy[%d]",
                        msgtype, systemid, functionid, routeinfo.toString(),
                        sendname, sendarg, nextnode, nextSession, isCopySend, copyCount);
    }

    public Long getPackageid() {
        return packageid;
    }

    public void setPackageid(Long packageid) {
        this.packageid = packageid;
    }

    public List<String> getTimeticks() {
        return timeticks;
    }

    public void setTimeticks(List<String> timeticks) {
        this.timeticks = timeticks;
    }

    public void addTimetick(String node, String plugin, long tick) {
        this.timeticks.add(String.format("%s;%s;%d", node, plugin, tick));
    }

    public List<EsbMsg> getResponse() {
        return response;
    }

    public void setResponse(List<EsbMsg> response) {
        this.response = response;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public boolean isCopySend() {
        return isCopySend;
    }

    public void setIsCopySend(boolean isCopySend) {
        this.isCopySend = isCopySend;
    }

    public Long getNextSession() {
        return nextSession;
    }

    public void setNextSession(Long nextSession) {
        this.nextSession = nextSession;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCompressAlgorithm() {
        return compressAlgorithm;
    }

    public void setCompressAlgorithm(String compressAlgorithm) {
        this.compressAlgorithm = compressAlgorithm;
    }

    public Integer getOriginLen() {
        return originLen;
    }

    public void setOriginLen(Integer originLen) {
        this.originLen = originLen;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }
}
