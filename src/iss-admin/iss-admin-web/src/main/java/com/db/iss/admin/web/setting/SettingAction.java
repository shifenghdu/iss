package com.db.iss.admin.web.setting;

import com.db.iss.admin.service.basic.ISettingService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.SettingPageVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by andy on 16/7/18.
 * @author andy.shif
 * 配置中心
 */
@Controller
public class SettingAction {

    @Autowired
    private ISettingService settingService;

    @RequestMapping("/setting")
    public String index(Model model,Long mid){
        SettingPageVo homePageVo = settingService.getHomePage((LoginUserVo) SecurityUtils.getSubject().getPrincipal(),mid);
        model.addAttribute("vo",homePageVo);
        return "home/home";
    }
}
