package com.ipharmacare.iss.connector;

import com.ipharmacare.iss.common.esb.EsbMsg;

public interface IConnectorCallBack {

	public abstract void onReceived(EsbMsg pack);

}
