package com.db.iss.connector;

import com.db.iss.common.esb.EsbMsg;

public interface IConnectorCallBack {

	public abstract void onReceived(EsbMsg pack);

}
