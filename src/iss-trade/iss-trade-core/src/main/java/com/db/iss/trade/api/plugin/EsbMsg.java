package com.db.iss.trade.api.plugin;
import org.msgpack.annotation.Message;

import java.util.List;
import java.util.Vector;

/**
 * Trade 基础消息
 * @author  andy.shif
 */
@Message
public class EsbMsg {

    public static final String CURRENT_VERSION = "v1.0.0";

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
    // 命名空间
    private String namespace;
    // 方法
    private String method;
    // 自定义路由标记
    private String tag;
    // 转发下一条节点名
    private String nextnode;
    // 业务消息包
    private List<byte[]> content;
    // 发送者插件名
    private String sendname;
    // 发送者附带参数
    private String sendarg;
    // 传入session id
    private long sessionId;
    //消息ID
    private Long packageid;
    //返回消息
    private EsbMsg response;

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public String getNextnode() {
        return nextnode;
    }

    public void setNextnode(String nextnode) {
        this.nextnode = nextnode;
    }

    public List<byte[]> getContent() {
        return content;
    }

    public void setContent(List<byte[]> content) {
        this.content = content;
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
                .format("msgtype:[%d] namespace:[%d] method:[%d] sender:[%s|%s] next[%s] session[%d]",
                        msgtype, namespace, method, sendname, sendarg, nextnode, sessionId);
    }

    public Long getPackageid() {
        return packageid;
    }

    public void setPackageid(Long packageid) {
        this.packageid = packageid;
    }

    public EsbMsg getResponse() {
        return response;
    }

    public void setResponse(EsbMsg response) {
        this.response = response;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
