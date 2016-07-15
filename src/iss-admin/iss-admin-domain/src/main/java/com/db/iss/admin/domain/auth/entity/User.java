package com.db.iss.admin.domain.auth.entity;


import com.db.iss.admin.domain.common.AbstractEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by andy on 16/7/2.
 * @author andy.shif
 * 用户
 */
@Entity
@Table(name = "ADMIN_USER")
public class User extends AbstractEntity {

    @Column(name = "NAME",length = 20, unique = true)
    private String userName;

    @Column(name = "PASSWORD",length = 64)
    private String password;

    @Transient
    private List<Role> roles;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
