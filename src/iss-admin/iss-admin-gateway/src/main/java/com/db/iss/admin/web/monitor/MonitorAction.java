package com.db.iss.admin.web.monitor;

import com.db.iss.admin.service.basic.IMonitorService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.MonitorPageVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by andy on 16/7/17.
 * @author andy.shif
 * 集群监控
 */
@Controller
public class MonitorAction {

    @Autowired
    private IMonitorService monitorService;

    @RequestMapping("monitor")
    public String index(Model model,Long mid){
        MonitorPageVo homePageVo = monitorService.getHomePage((LoginUserVo) SecurityUtils.getSubject().getPrincipal(),mid);
        model.addAttribute("vo",homePageVo);
        return "home/home";
    }
}

