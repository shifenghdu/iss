package com.db.iss.admin.web.auth.shiro;

import com.db.iss.admin.domain.auth.IUserService;
import com.db.iss.admin.domain.auth.entity.Role;
import com.db.iss.admin.domain.auth.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 用户注册AuthorizingRealm
 */
@Service
public class UserAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private IUserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取登录的用户名
        String loginName=(String) principals.fromRealm(getName()).iterator().next();
        User user=userService.getUserByUserName(loginName);

        if(user!=null){
            SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
            //登录的用户有多少个角色
            info.setRoles(userService.getRolesByUserName(loginName));
            info.addStringPermissions(userService.getPermissionsByUserName(loginName));
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        User user = userService.getUserByUserName(usernamePasswordToken.getUsername());
        if(user != null) {
            return new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), getName());
        }
        return null;
    }

}
