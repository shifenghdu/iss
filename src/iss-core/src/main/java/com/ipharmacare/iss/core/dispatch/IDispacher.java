package com.ipharmacare.iss.core.dispatch;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.plugin.IPlugin;

public interface IDispacher extends IPlugin {

	public void pollMsg(Long threadId, EsbMsg esbMsg);

	public EsbMsg getMsg(Long threadId);
}