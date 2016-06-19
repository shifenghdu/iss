package com.db.iss.core.acceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.db.iss.core.Laucher;
import com.db.iss.core.acceptor.mina.AccessAcceptor;
import com.db.iss.core.config.BaseConfig;
import com.db.iss.core.msgpack.CommonCodeFactory;
import com.db.iss.core.router.IRouter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.db.iss.common.esb.EsbMsg;

@Service("p_acceptor")
public class MinaAcceptor implements IAcceptor {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IRouter router;

	@Autowired
	private BaseConfig config;

	private AccessAcceptor acceptor = null;

	private String pluginName = "acceptor";

	private String nodeName;

	private ConcurrentHashMap<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();

	private static AtomicLong count = new AtomicLong(0);

	@Autowired
	private CommonCodeFactory codecFactory;

	public CommonCodeFactory getCodecFactory() {
		return codecFactory;
	}

	@Override
	public boolean transMsg(EsbMsg pack) {
		pack.addTimetick(getNodeName(), getPluginName(), System.nanoTime());
		long current = count.incrementAndGet();
		if (current % 1000 == 0) {
			logger.info("Acceptor发送消息数[{}]", current);
		}
		synchronized (sessionMap) {
			if (sessionMap.containsKey(pack.getSendarg())) {
				IoSession session = sessionMap.get(pack.getSendarg());
				session.getConfig().setThroughputCalculationInterval(0);
				session.write(pack);
			} else {
				logger.warn("客户端链接不存在消息丢弃 msg[{}]", pack);
			}
		}
		return true;
	}

	@Override
	public void onStart(ApplicationContext context) {
		String port = config.getPluginConfig(pluginName).attributeValue("port");
		this.nodeName = Laucher.nodeName;
		if (!port.isEmpty()) {
			acceptor = new AccessAcceptor(this);
			acceptor.bind(Integer.valueOf(port));
		}
	}

	@Override
	public void onStop() {

	}

	public boolean sendMsg(EsbMsg pack) {
		return router.transMsg(pack);
	}

	public String getPluginName() {
		return pluginName;
	}

	public void addSession(IoSession session) {
		sessionMap.put(String.valueOf(session.getId()), session);
	}

	public String getNodeName() {
		return nodeName;
	}

}
