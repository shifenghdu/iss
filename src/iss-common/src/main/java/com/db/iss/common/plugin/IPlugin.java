package com.db.iss.common.plugin;

import com.db.iss.common.esb.EsbMsg;
import org.springframework.context.ApplicationContext;

public interface IPlugin {

	/**
	 * 插件间传递消息接口
	 * 
	 * @param pack
	 * @return
	 */
	public boolean transMsg(EsbMsg pack);

	/**
	 * 插件启动接口
	 */
	public void onStart(ApplicationContext context);

	/**
	 * 插件停止接口
	 */
	public void onStop();

}
