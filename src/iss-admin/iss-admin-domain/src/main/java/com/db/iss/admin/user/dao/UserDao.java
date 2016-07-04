package com.db.iss.admin.user.dao;

import com.db.iss.admin.common.AbstractDao;
import com.db.iss.admin.user.entity.User;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by apple on 16/7/5.
 */
@Transactional
@Repository
public class UserDao extends AbstractDao<User>{



}
