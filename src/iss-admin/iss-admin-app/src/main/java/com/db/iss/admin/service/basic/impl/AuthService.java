package com.db.iss.admin.service.basic.impl;

import com.db.iss.admin.domain.basic.User;
import com.db.iss.admin.service.basic.IAuthService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by andy on 16/7/17.
 * @author andy.shif
 * 鉴权服务
 */
@Service
@Transactional
public class AuthService implements IAuthService{

    @Override
    public LoginUserVo getUserByName(String userName) {
        User user = User.getUserByName(userName);
        return LoginUserVo.fromUser(user);
    }

}
