package com.db.iss.admin.service.basic;


import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.page.HomePageVo;

/**
 * Created by apple on 16/7/16.
 * @author andy.shif
 * 首页服务
 */
public interface IHomeService {

    HomePageVo getHomePage(LoginUserVo loginUserVo);

}
