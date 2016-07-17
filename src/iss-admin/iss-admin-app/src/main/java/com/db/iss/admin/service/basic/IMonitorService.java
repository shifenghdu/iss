package com.db.iss.admin.service.basic;

import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.HomePageVo;
import com.db.iss.admin.service.basic.vo.page.MonitorPageVo;

/**
 * Created by andy on 16/7/17.
 * @author andy.shif
 * 集群监控
 */
public interface IMonitorService {

    MonitorPageVo getHomePage(LoginUserVo loginUserVo);
}
