package com.ipharmacare.iss.core.cluster;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeGroup {

    private List<IoSession> sessions = new ArrayList<IoSession>();

    private int current = 0;

    /**
     * 添加节点至节点组
     *
     * @param session
     */
    public void addNode(IoSession session) {
        synchronized (sessions) {
            sessions.add(session);
        }
    }

    /**
     * 获取节点组中节点的数量
     *
     * @return
     */
    public int getNodeCount() {
        return sessions.size();
    }

    private void next() {
        current++;
        if (current >= sessions.size()) {
            current -= (sessions.size());
        }
    }

    /**
     * 获取可用的节点
     *
     * @return
     */
    public IoSession getNode() {
        IoSession session = null;
        synchronized (sessions) {
            try {
                session = sessions.get(current);
                while (session.isClosing()) {
                    sessions.remove(current);
                    session = sessions.get(current);
                }
                next();
            } catch (Exception e) {
                current = 0;
                if (sessions.size() != 0) {
                    return sessions.get(current);
                } else {
                    return null;
                }
            }
        }
        return session;
    }

    public IoSession getNode(Long sessionid) {
        IoSession session = null;
        synchronized (sessions) {
            Iterator<IoSession> iterator = sessions.iterator();
            while (iterator.hasNext()) {
                IoSession s = iterator.next();
                if (!s.isClosing() && s.getId() == sessionid) {
                    session = s;
                }
            }
        }
        return session;
    }

    private void checkSession() {
        synchronized (sessions) {
            Iterator<IoSession> iterator = sessions.iterator();
            while (iterator.hasNext()) {
                IoSession session = iterator.next();
                if (session.isClosing()) {
                    iterator.remove();
                }
            }
        }
    }

    public List<IoSession> getNodes() {
        checkSession();
        return sessions;
    }
}
