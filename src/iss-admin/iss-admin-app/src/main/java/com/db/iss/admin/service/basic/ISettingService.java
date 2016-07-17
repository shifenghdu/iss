package com.db.iss.admin.service.basic;

import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.MonitorPageVo;
import com.db.iss.admin.service.basic.vo.page.SettingPageVo;

/**
 * Created by andy on 16/7/18.
 * @author andy.shif
 * 配置服务
 */
public interface ISettingService {

    SettingPageVo getHomePage(LoginUserVo loginUserVo);

}
