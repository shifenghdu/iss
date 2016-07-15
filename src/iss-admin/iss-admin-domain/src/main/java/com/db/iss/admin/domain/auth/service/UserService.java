package com.db.iss.admin.domain.auth.service;

import com.db.iss.admin.domain.auth.IUserService;
import com.db.iss.admin.domain.auth.dao.UserDao;
import com.db.iss.admin.domain.auth.entity.Permission;
import com.db.iss.admin.domain.auth.entity.Role;
import com.db.iss.admin.domain.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 用户服务实现
 */
@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserDao userDao;


    @Override
    public User getUserByUserName(String userName) {
        return userDao.getUserByName(userName);
    }

    @Override
    public Set<String> getRolesByUserName(String userName) {
//        User user = userDao.getUserByName(userName);
//        Set<String> result = new LinkedHashSet<>();
//        Set<Role> roles = user.getRoles();
//        if (roles != null){
//            for (Role role : roles) {
//                result.add(role.getRoleName());
//            }
//        }
        return null;
    }

    @Override
    public Set<String> getPermissionsByUserName(String userName) {
        User user = userDao.getUserByName(userName);
//        Set<Role> roles = user.getRoles();
//        Set<String> permissions = new LinkedHashSet<>();
//        if (roles != null){
//            for (Role role : roles) {
//                Set<Permission> permissionSet = role.getPermissions();
//                if(permissionSet != null) {
//                    for (Permission permission : permissionSet) {
//                        permissions.add(permission.getPermissionName());
//                    }
//                }
//            }
//        }
        return null;
    }

    @Override
    public void addUser(User user) {
        userDao.addUser(user);
    }


}
