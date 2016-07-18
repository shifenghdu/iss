package com.db.iss.admin.service.basic.impl;

import com.db.iss.admin.service.basic.AbstractBasicService;
import com.db.iss.admin.service.basic.ISettingService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.SettingPageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by andy on 16/7/18.
 * @author andy.shif
 * 配置服务实现
 */
@Service
@Transactional
public class SettingService extends AbstractBasicService implements ISettingService {

    public SettingService() {
        super(new SettingPageVo());
    }

    @Override
    public SettingPageVo getHomePage(LoginUserVo loginUserVo,Long mid) {
        buildMenus(loginUserVo,1L);
        buildMenuLine(mid);
        return (SettingPageVo) getBasicVo();
    }
}
