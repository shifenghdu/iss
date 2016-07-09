package com.db.iss.admin.domain.auth.dao;

import com.db.iss.admin.domain.common.AbstractDao;
import com.db.iss.admin.domain.auth.entity.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by andy on 16/7/5.
 * @author andy.shif
 */
@Repository
@Transactional
public class UserDao extends AbstractDao<User>{

    /**
     * 通过用户名获取用户信息
     * @param userName
     * @return
     */
    public User getUserByName(String userName){
        return (User) getCurrentSession().createCriteria(User.class)
                .add(Restrictions.eq("userName",userName)).uniqueResult();
    }


    /**
     * 新增用户
     * @param user
     */
    public void addUser(User user){
        getCurrentSession().saveOrUpdate(user);
    }

}
