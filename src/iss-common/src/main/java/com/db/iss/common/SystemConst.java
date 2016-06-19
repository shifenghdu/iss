package com.db.iss.common;

public class SystemConst {

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


}
