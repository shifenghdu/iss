package com.db.iss.admin.service.basic.impl;

import com.db.iss.admin.service.basic.AbstractBasicService;
import com.db.iss.admin.service.basic.IHomeService;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.HomePageVo;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * 基础服务
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional
public class HomeService extends AbstractBasicService implements IHomeService {

    public HomeService() {
        super(new HomePageVo());
    }

    @Override
    public HomePageVo getHomePage(LoginUserVo loginUserVo) {
        buildMenus(loginUserVo,-1L);
        buildMenuLine(-1L);
        return (HomePageVo) getBasicVo();
    }
}
