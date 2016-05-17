package com.ipharmacare.iss.common.dispatch;

public interface IBizProcessor {

	public int getSystemId();

	public int getFunctionId();

	public byte[] doProcess(IBizContext bizContext, byte[] inMsg);

}
