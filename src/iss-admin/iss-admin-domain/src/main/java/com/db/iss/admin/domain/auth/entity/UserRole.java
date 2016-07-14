package com.db.iss.admin.domain.auth.entity;

import com.db.iss.admin.domain.common.AbstractEntity;

import javax.jws.soap.SOAPBinding;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by andy on 16/7/14.
 * @author andy.shif
 * 用户角色关系
 */
@Entity
@Table(name = "ADMIN_USER_ROLE")
public class UserRole extends AbstractEntity{

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Transient
    private User user;

    @Transient
    private Role role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
