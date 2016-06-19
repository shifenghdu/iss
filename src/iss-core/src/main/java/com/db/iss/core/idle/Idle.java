package com.db.iss.core.idle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.db.iss.common.esb.EsbMsg;
import com.db.iss.common.plugin.IPlugin;
import com.db.iss.core.router.IRouter;

@Service("p_idle")
public class Idle implements IPlugin {

	@Autowired
	private IRouter router;

	@Override
	public boolean transMsg(EsbMsg pack) {
		pack.changeToResponse();
		router.transMsg(pack);
		return true;
	}

	@Override
	public void onStart(ApplicationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
