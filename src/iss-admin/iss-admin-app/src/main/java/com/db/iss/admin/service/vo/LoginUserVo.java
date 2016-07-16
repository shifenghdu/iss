package com.db.iss.admin.service.vo;

import com.db.iss.admin.domain.basic.User;

import java.io.Serializable;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * 当前登录用户
 */
public class LoginUserVo implements Serializable {

    private Long userId;

    private String userName;
    public Long getUserId() {
            return userId;
        }

    public void setUserId(Long userId) {
            this.userId = userId;
        }

    public String getUserName() {
            return userName;
        }

    public void setUserName(String userName) {
            this.userName = userName;
        }

    /**
     * domain User 转换成 LoginUserVo
     * @param user
     * @return
     */
    public static LoginUserVo fromUser(User user){
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setUserName(user.getUserName());
        loginUserVo.setUserId(user.getId());
        return loginUserVo;
    }
}