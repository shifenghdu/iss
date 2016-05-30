package com.ipharmacare.iss.core.component;

import com.ipharmacare.iss.common.dispatch.IBizContext;

/**
 * Created by andy on 2016/1/7.
 */
public class BizContextHolder {
	private static  ThreadLocal<IBizContext> contextThreadLocal = new ThreadLocal<IBizContext>();

	public static IBizContext getBizContext(){
		return contextThreadLocal.get();
	}

	public static void setBizContext(IBizContext context){
		contextThreadLocal.set(context);
	}
}
