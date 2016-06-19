package com.db.iss.core.component;

import com.db.iss.common.annotation.Function;
import com.db.iss.common.annotation.Remote;
import com.db.iss.common.dispatch.IBizContext;
import com.db.iss.common.util.PackageScannerUtil;
import com.db.iss.common.dispatch.IBizMgr;
import com.db.iss.common.dispatch.IBizProcessor;
import com.db.iss.common.dispatch.IBizRegister;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by andy on 2015/12/26.
 */
public class ComponentMgr implements IBizMgr {
	@Override
	public void onRegister(IBizRegister iBizRegister,IBizContext context) {
		BizContextHolder.setBizContext(context);
		ApplicationContext.initFromConfig();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		Set<Class<?>> classSet = PackageScannerUtil.getClasses(ApplicationContext.getScanPackage());

		for (Class<?> classz : classSet) {
			Remote remote = classz.getAnnotation(Remote.class);
			if (remote != null) {
				Method[] methods = classz.getDeclaredMethods();
				for (Method method : methods) {
					Function function = method.getAnnotation(Function.class);
					if (function != null) {
						try {
							IBizProcessor proxy = new ProcessorHandler().getProxy(classz, method);
							int systemno = (int)remote.annotationType().getDeclaredMethod("system",null).invoke(remote,null);
							int functionid = (int) function.annotationType().getDeclaredMethod("function", null).invoke(function, null);
//							System.err.println(String.format("Load System: [%d],Function: [%d]", systemno,functionid));
							iBizRegister.register(systemno,functionid, proxy);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
