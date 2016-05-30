package com.ipharmacare.iss.core.scheduler;

import com.ipharmacare.iss.common.esb.EsbMsg;

public class TaskItem implements Runnable {

	private int systemid;

	private int functionid;

	private SpringScheduler scheduler;

	public TaskItem(int systemid, int functionid, SpringScheduler scheduler) {
		this.systemid = systemid;
		this.functionid = functionid;
		this.scheduler = scheduler;
	}

	@Override
	public void run() {
		EsbMsg msg = new EsbMsg();
		msg.setMsgtype(EsbMsg.MSGTYPE_REQ);
		msg.setSystemid(systemid);
		msg.setFunctionid(functionid);
		msg.setSendname(SpringScheduler.pluginName);
		scheduler.getRouter().transMsg(msg);
	}

}
