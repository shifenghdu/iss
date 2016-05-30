package com.ipharmacare.iss.core.testcase;

import java.util.List;

import org.dom4j.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.common.plugin.IPlugin;
import com.ipharmacare.iss.core.config.BaseConfig;
import com.ipharmacare.iss.core.router.StaticRouter;

public class ISSTest implements Runnable {

	private static ApplicationContext ctx;

	private int count = 1000;

	private StaticRouter router;

	private int THREAD_COUNT = 5;

	private Thread[] threads = new Thread[THREAD_COUNT];

	//@Test
	public void test() throws InterruptedException {

		System.out
				.println("************************************ Begin Start ISS Runtime Envment *********************************");
		ctx = new ClassPathXmlApplicationContext(
				new String[] { "appcontext.xml" });
		BaseConfig config = ctx.getBean("s_config", BaseConfig.class);
		@SuppressWarnings("unchecked")
		List<Element> elements = config.getDocument().getRootElement()
				.element("plugins").elements("plugin");
		for (Element element : elements) {
			String pluginName = "p_" + element.attributeValue("name");
			IPlugin plugin = ctx.getBean(pluginName, IPlugin.class);
			if (plugin != null) {
				plugin.onStart(ctx);
				System.out.println("plugin [" + element.attributeValue("name")
						+ "] start succed");
			} else {
				System.err.println("plugin [" + element.attributeValue("name")
						+ "] start failed");
			}
		}

		System.out
				.println("************************************ Finish Start ISS Runtime Envment ********************************");

		router = ctx.getBean("p_router", StaticRouter.class);

		for (int i = 0; i < THREAD_COUNT; i++) {
			threads[i] = new Thread(this);
		}

		long begin = System.nanoTime();
		for (int i = 0; i < THREAD_COUNT; i++) {
			threads[i].start();
		}

		for (int i = 0; i < THREAD_COUNT; i++) {
			threads[i].join();
		}
		long end = System.nanoTime();
		System.err.println("测试结果 并发数[" + THREAD_COUNT + "] 总笔数[" + THREAD_COUNT
				* count + "] 总耗时 [" + (end - begin) / 1000 + "]us TPS["
				+ ((double) (THREAD_COUNT * count))
				/ ((end - begin))*1000000000 + "]");
		Thread.sleep(10000);

	}

	@Override
	public void run() {
		for (int i = 0; i < count; i++) {
			EsbMsg msg = new EsbMsg();
			msg.setSystemid(1000);
			msg.setFunctionid(1001);
			msg.setSendname("acceptor");
			msg.setSendarg("1");
			router.doRouter(msg);
		}
	}
}
