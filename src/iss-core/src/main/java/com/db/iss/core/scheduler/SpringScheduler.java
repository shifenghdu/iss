package com.db.iss.core.scheduler;

import java.util.List;

import com.db.iss.core.config.BaseConfig;
import com.db.iss.core.router.IRouter;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.db.iss.common.esb.EsbMsg;

@Service("p_scheduler")
public class SpringScheduler implements IScheduler {

	@Autowired
	private IRouter router;

	public static final String pluginName = "scheduler";

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private BaseConfig config;

	/**
	 * 不处理请求
	 */
	@Override
	public boolean transMsg(EsbMsg pack) {
		return true;
	}

	@Override
	public void onStart(ApplicationContext context) {
		@SuppressWarnings("unchecked")
		List<Element> tasklist = config.getPluginConfig(pluginName)
				.element("tasks").elements();
		for (Element task : tasklist) {
			String exp = task.attributeValue("corn");
			int systemid = Integer.valueOf(task.attributeValue("systemid"));
			int functionid = Integer.valueOf(task.attributeValue("functionid"));
			Trigger trigger = new CronTrigger(exp);
			TaskItem item = new TaskItem(systemid, functionid, this);
			taskScheduler.schedule(item, trigger);
		}
	}

	@Override
	public void onStop() {

	}

	public IRouter getRouter() {
		return router;
	}

}
