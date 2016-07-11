package com.ipharmacare.iss.core.acceptor;

import com.ipharmacare.iss.common.esb.EsbMsg;
import com.ipharmacare.iss.core.Laucher;
import com.ipharmacare.iss.core.acceptor.mina.AccessAcceptor;
import com.ipharmacare.iss.core.config.BaseConfig;
import com.ipharmacare.iss.core.msgpack.CommonCodeFactory;
import com.ipharmacare.iss.core.router.IRouter;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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

    private final int RETRY_TIMES = 3;

    private final long WRITE_WAIT_TIME = 10000L;

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
                if(!writeSession(session,pack,RETRY_TIMES)){
                    logger.warn("发送消息至客户端失败 msg[{}]", pack);
                    return false;
                }
			} else {
				logger.warn("客户端链接不存在消息丢弃 msg[{}]", pack);
			}
		}
		return true;
	}

    private boolean writeSession(IoSession session,EsbMsg msg,int times){
        int t = 0;
        if(session == null || session.isClosing() || msg == null) return false;
        while (t < times) {
//            if(session.write(msg).awaitUninterruptibly().isWritten()){
//                return true;
//            }
            WriteFuture future = session.write(msg);
            if(future.awaitUninterruptibly(WRITE_WAIT_TIME)){
                if(future.isWritten()) {
                    return true;
                }
            }
            t ++;
        }
        session.close(true);
        return false;
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
