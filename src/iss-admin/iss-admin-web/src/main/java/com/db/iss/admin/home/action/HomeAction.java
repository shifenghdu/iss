package com.db.iss.admin.home.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by andy on 16/7/1.
 * @author andy.shif
 * 管理主页Action
 */
@Controller
@RequestMapping("home.do")
public class HomeAction {

    @RequestMapping(params = "method=1", method= RequestMethod.GET)
    public String index(){
        return "index";
    }

}
