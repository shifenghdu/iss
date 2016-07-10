package com.db.iss.admin.web.home;

import com.db.iss.admin.web.common.AbstractHomeAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by andy on 16/7/1.
 * @author andy.shif
 * 主页
 */
@Controller
public class HomeAction extends AbstractHomeAction {

    @RequestMapping("home")
    public String index(){
        return "home/home";
    }

}
