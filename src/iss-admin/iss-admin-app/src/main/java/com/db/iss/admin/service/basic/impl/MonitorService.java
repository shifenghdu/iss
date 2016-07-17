package com.db.iss.admin.service.basic.impl;

import com.db.iss.admin.service.basic.AbstractBasicService;
import com.db.iss.admin.service.basic.IMonitorService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.MonitorPageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by andy on 16/7/17.
 * @author andy.shif
 * 集群监控服务实现
 */
@Service
@Transactional
public class MonitorService extends AbstractBasicService implements IMonitorService {

    public MonitorService() {
        super(new MonitorPageVo());
    }

    @Override
    public MonitorPageVo getHomePage(LoginUserVo loginUserVo) {
        buildMenus(loginUserVo,1L);
        return (MonitorPageVo) getBasicVo();
    }
}
