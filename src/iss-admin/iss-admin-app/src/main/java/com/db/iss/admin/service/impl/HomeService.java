package com.db.iss.admin.service.impl;

import com.db.iss.admin.service.AbstractBasicService;
import com.db.iss.admin.service.IHomeService;
import com.db.iss.admin.service.vo.page.HomePageVo;
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
    public HomePageVo getHomePage() {
        buildMenus(-1L);
        return (HomePageVo) getBasicVo();
    }
}
