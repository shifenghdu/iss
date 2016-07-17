package com.db.iss.admin.service.basic;
import com.db.iss.admin.service.basic.vo.LoginUserVo;

/**
 * Created by andy on 16/7/17.
 * @author andy.shif
 * 鉴权服务
 */
public interface IAuthService {

    /**
     * 根据用户名获取登录用户信息
     * @param userName
     * @return
     */
    LoginUserVo getUserByName(String userName);

}
