package com.ipharmacare.iss.core.component;

import com.ipharmacare.iss.common.annotation.Function;
import com.ipharmacare.iss.common.annotation.Remote;
import com.ipharmacare.iss.common.dispatch.IBizContext;
import com.ipharmacare.iss.common.dispatch.IBizMgr;
import com.ipharmacare.iss.common.dispatch.IBizProcessor;
import com.ipharmacare.iss.common.dispatch.IBizRegister;
import com.ipharmacare.iss.common.util.PackageScannerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by andy on 2015/12/26.
 */
public class ComponentMgr implements IBizMgr {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onRegister(IBizRegister iBizRegister,IBizContext context) {
		BizContextHolder.setBizContext(context);
		ApplicationContext.initFromConfig();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		Set<Class<?>> classSet = PackageScannerUtil.getClasses(ApplicationContext.getScanPackage());

		for (Class<?> classz : classSet) {
            try {
                ApplicationContext.getBean(classz);
                Remote remote = classz.getAnnotation(Remote.class);
                if (remote != null) {
                    Method[] methods = classz.getDeclaredMethods();
                    for (Method method : methods) {
                        Function function = method.getAnnotation(Function.class);
                        if (function != null) {
                            IBizProcessor proxy = new ProcessorHandler().getProxy(classz, method);
                            int systemno = (int) remote.annotationType().getDeclaredMethod("system", null).invoke(remote, null);
                            int functionid = (int) function.annotationType().getDeclaredMethod("function", null).invoke(function, null);
//							System.err.println(String.format("Load System: [%d],Function: [%d]", systemno,functionid));
                            iBizRegister.register(systemno, functionid, proxy);
                        }
                    }
                }
            }catch (NoSuchBeanDefinitionException e1){
                logger.error("未找到接口对应实现 {}",classz.getName());
            } catch (Throwable e){
                logger.error("注册接口实现错误",e);
            }
		}
	}
}
