package com.db.iss.admin.domain.auth.dao;

import com.db.iss.admin.domain.common.AbstractDao;
import com.db.iss.admin.domain.auth.entity.User;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by apple on 16/7/5.
 */
@Transactional
@Repository
public class UserDao extends AbstractDao<User>{



}
