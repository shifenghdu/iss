package com.db.iss.admin.web.auth.shiro;

import com.db.iss.admin.domain.basic.User;
import com.db.iss.admin.service.vo.LoginUserVo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 用户注册AuthorizingRealm
 */
@Service
public class UserAuthorizingRealm extends AuthorizingRealm {

    /**
     * shiro 鉴权回调
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取登录的用户名
        User user = (User) principals.fromRealm(getName()).iterator().next();

//        if(user!=null){
//            SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
//            //登录的用户有多少个角色
//            info.addObjectPermissions(userService.getPermissionsByUserId(user.getId()));
//            info.setRoles(userService.getRolesByUserId(user.getId()));
//            info.addStringPermissions(userService.getPermissionsByUserId(user.getId()));
//            return info;
//        }
        return null;
    }

    /**
     * shiro 登录回调
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    @Transactional
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        User user = User.getUserByName(usernamePasswordToken.getUsername());
        if(user != null) {
            return new SimpleAuthenticationInfo(LoginUserVo.fromUser(user), user.getPassword(), getName());
        }
        return null;
    }

}
