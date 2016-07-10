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
@Table(name = "admin_user")
public class User extends AbstractEntity {

    @Column(name = "name",length = 20, unique = true)
    private String userName;

    @Column(name = "password",length = 64)
    private String password;

    @ManyToMany(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinTable(
            name = "admin_user_role",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="role_id")
            })
    private Set<Role> roles;

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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
