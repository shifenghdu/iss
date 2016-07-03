package com.db.iss.admin.user.util;

import com.db.iss.admin.user.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by andy on 16/7/2.
 * 用户相关操作方法
 */
public class UserSupport {

    private static final String SESSION_LOGIN_USER = "login_user";

    public static User getSessionLoginUser(HttpServletRequest httpServletRequest){
       return (User) httpServletRequest.getSession().getAttribute(SESSION_LOGIN_USER);
    }

    public static void setSessionLoginUser(HttpServletRequest httpServletRequest,User user){
        httpServletRequest.getSession().setAttribute(SESSION_LOGIN_USER,user);
    }
}
