package com.db.iss.admin.web.home;

import com.db.iss.admin.service.IHomeService;
import com.db.iss.admin.service.vo.page.HomePageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by andy on 16/7/1.
 * @author andy.shif
 * 主页
 */
@Controller
public class HomeAction {

    @Autowired
    private IHomeService homeService;

    @RequestMapping("home")
    public String index(Model model){
        HomePageVo homePageVo = homeService.getHomePage();
        model.addAttribute("vo",homePageVo);
        return "home/home";
    }

}
