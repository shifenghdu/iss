package com.db.iss.admin.user.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by andy on 16/7/2.
 * @author andy.shif
 * 登录处理
 */
@Controller
@RequestMapping("user")
public class LoginAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String name,String password){
        logger.info("user login name: [{}] passwd: [{}]",name,password);
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        Subject user = SecurityUtils.getSubject();
        try{
            user.login(token);
        }catch (AuthenticationException e) {
            return "redirect: /user/login";
        }
        //登录成功
        return "redirect: /home";
    }


    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String loginView(){
        return "user/login";
    }

}
