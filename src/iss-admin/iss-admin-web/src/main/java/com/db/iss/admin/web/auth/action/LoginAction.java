package com.db.iss.admin.web.auth.action;

import com.db.iss.admin.domain.auth.IUserService;
import com.db.iss.admin.domain.auth.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * Created by andy on 16/7/2.
 * @author andy.shif
 * 登录处理
 */
@Controller
@RequestMapping("auth")
public class LoginAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String name,String password){
        logger.info("shiro login name: [{}] passwd: [{}]",name,password);
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        token.setRememberMe(true);
        Subject user = SecurityUtils.getSubject();
        try{
            user.login(token);
        }catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
        //登录成功
        return "redirect:/home";
    }

    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String loginView(){
        return "auth/login";
    }

    @RequestMapping(value = "add",method = RequestMethod.GET)
    public String addUser(){
        User user = new User();
        user.setUserName("admin");
        user.setPassword("123");
        user.setCreateTime(new Date());
        user.setLastModifyTime(new Date());
        userService.addUser(user);
        return "home";
    }



}
