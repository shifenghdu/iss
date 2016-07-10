package com.db.iss.admin.web.auth;

import com.db.iss.admin.domain.auth.IUserService;
import com.db.iss.admin.domain.auth.entity.User;
import com.db.iss.admin.web.common.AbstractAction;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by andy on 16/7/2.
 * @author andy.shif
 * 登录处理
 */
@Controller
@RequestMapping("auth")
public class LoginAction extends AbstractAction{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IUserService userService;

    /**
     * 登录操作
     * @param name
     * @param password
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(String name, String password, HttpServletRequest request){
        logger.info("shiro login name: [{}] [{}]",name,password);
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        token.setRememberMe(true);
        try{
            getSubject().login(token);
        }catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
        request.getSession().setAttribute("loginUserName",token.getUsername());
        //登录成功
        return "redirect:/home";
    }

    /**
     * 登录页面
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String loginView(){
        return "auth/login";
    }


    /**
     * 添加测试账号
     * @return
     */
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

    /**
     * 退出登录
     * @return
     */
    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
        }
        return "redirect:/auth/login";
    }
}
