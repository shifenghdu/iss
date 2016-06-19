package com.db.iss.common.dispatch;


import java.util.List;

public interface IBizContext {

	public abstract byte[] call(int systemId, int functionId, byte[] msg);
	public abstract byte[] call(int systemId, int functionId,String tag, byte[] msg);


	public abstract void post(int systemId, int functionId, byte[] msg);
	public abstract void post(int systemId, int functionId, String tag, byte[] msg);

	public abstract List<byte[]> multiCall(int systemId, int functionId, byte[] msg);
	public abstract List<byte[]> multiCall(int systemId, int functionId,String tag, byte[] msg);

}
