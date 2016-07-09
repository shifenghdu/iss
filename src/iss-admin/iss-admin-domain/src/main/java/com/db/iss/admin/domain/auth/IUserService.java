package com.db.iss.admin.domain.auth;

import com.db.iss.admin.domain.auth.entity.User;

import java.util.Set;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 通过用户名获取用户信息
     * @param userName
     * @return
     */
    User getUserByUserName(String userName);

    /**
     * 通过用户名获取角色信息
     * @param userName
     * @return
     */
    Set<String> getRolesByUserName(String userName);

    /**
     * 通过用户名获取权限信息
     * @param userName
     * @return
     */
    Set<String> getPermissionsByUserName(String userName);


    /**
     * 新增用户
     * @param user
     */
    void addUser(User user);

}
