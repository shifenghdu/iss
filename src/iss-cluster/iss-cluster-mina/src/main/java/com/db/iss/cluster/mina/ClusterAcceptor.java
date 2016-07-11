package com.db.iss.cluster.mina;

import com.db.iss.cluster.mina.codec.ClusterCodecFactory;
import com.db.iss.cluster.mina.handler.ServerMsgHandler;
import com.db.iss.core.compressor.CompressorType;
import com.db.iss.core.plugin.AbstractTransportPlugin;
import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.core.serializer.SerializerType;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ClusterAcceptor {

    private NioSocketAcceptor acceptor;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final int SIZE_128K = 131072;

    private final int RETRY_TIMES = 3;

    private final long WRITE_TIME_OUT = 10000L;

    private Map<Long,IoSession> sessionMap = new ConcurrentHashMap<>();

    public ClusterAcceptor(SerializerType type, CompressorType compressorType, AbstractTransportPlugin plugin) {
        acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress(false);
        acceptor.getSessionConfig().setReuseAddress(false);
		acceptor.getSessionConfig().setReceiveBufferSize(SIZE_128K);
		acceptor.getSessionConfig().setReadBufferSize(SIZE_128K);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(10240);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClusterCodecFactory(type,compressorType)));
        //acceptor.getFilterChain().addLast("pool", new ExecutorFilter(Runtime.getRuntime().availableProcessors() + 1));
        acceptor.setHandler(new ServerMsgHandler(plugin,this));
    }

    /**
     * 监听端口
     * @param port
     * @throws IOException
     */
    public void bind(int port) throws IOException {
        acceptor.bind(new InetSocketAddress(port));
    }

    /**
     * 写入返回数据
     * @param msg
     * @return
     */
    public boolean write(EsbMsg msg){
        IoSession session = sessionMap.get(msg.getSessionId());
        if(session != null && !session.isClosing()) {
            int times = 0;
            while (times < RETRY_TIMES) {
                times++;
                WriteFuture future = session.write(msg);
                future.awaitUninterruptibly(WRITE_TIME_OUT);
                if (future.isWritten()) {
                    return true;
                }
            }
        }
        if(session != null) {
            logger.error("url [{}] write response message [{}] error", session.getAttribute("url"), msg);
        }else{
            logger.error("url [null] write response message [{}] error", msg);
        }
        session.close(true);
        return false;
    }

    /**
     * 添加新建session
     * @param id
     * @param session
     */
    public void addSession(Long id, IoSession session){
        sessionMap.put(id, session);
    }

    /**
     * 删除已关闭session
     * @param id
     */
    public void removeSession(Long id){
        sessionMap.remove(id);
    }

    @Override
    protected void finalize() throws Throwable {
        acceptor.unbind();
        acceptor.dispose();
        super.finalize();
    }

}
