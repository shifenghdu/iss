package com.db.iss.core.dispatch;

import com.db.iss.common.esb.EsbMsg;
import com.db.iss.common.plugin.IPlugin;

public interface IDispacher extends IPlugin {

	public void pollMsg(Long threadId, EsbMsg esbMsg);

	public EsbMsg getMsg(Long threadId);
}