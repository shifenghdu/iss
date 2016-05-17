package com.ipharmacare.iss.common.dispatch;

public interface IBizRegister {
	
	public  void register(int systemno,int functionid,IBizProcessor bizProcessor);

}
