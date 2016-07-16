package com.db.iss.admin.domain.common;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Created by andy on 16/7/5.
 * @author andy.shif
 * 仓库实现类
 */
@Service
public class Repository {

    /**
     * hibernate session工厂
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 获取hibernate session工厂
     * @return
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 获取当前session
     * @return
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存实体对象
     * @param o
     * @return
     */
    public Serializable save(Object o) {
        return getCurrentSession().save(o);
    }

    /**
     * 删除实体对象
     * @param o
     */
    public void delete(Object o) {
        getCurrentSession().delete(o);
    }

    /**
     * 更新实体对象
     * @param o
     */
    public void update(Object o) {
        getCurrentSession().update(o);
    }

    /**
     * 合并实体对象
     * @param o
     * @return
     */
    public <T> T merge(T o) {
        return (T) getCurrentSession().merge(o);
    }

    /**
     * Hql查询实体对象列表
     * @param hql
     * @param params
     * @return
     */
    public <S extends AbstractEntity> List<S> queryForList(String hql, Object... params) {
        Query query = getCurrentSession().createQuery(hql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
        }
        return query.list();
    }

    /**
     * 查询单体对象
     * @param hql
     * @param params
     * @return
     */
    public <S extends AbstractEntity> S query(String hql, Object... params) {
        Query query = getCurrentSession().createQuery(hql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
        }
        return (S) query.uniqueResult();
    }

    
}
