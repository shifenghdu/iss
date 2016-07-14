package com.db.iss.admin.domain.auth.entity;

import com.db.iss.admin.domain.common.AbstractEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 角色
 */
@Entity
@Table(name = "ADMIN_ROLE")
public class Role extends AbstractEntity {

    @Column(name = "NAME",length = 20)
    private String roleName;

    @Transient
    private UserRole userRole;

    @Transient
    private RolePermission rolePermission;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public RolePermission getRolePermission() {
        return rolePermission;
    }

    public void setRolePermission(RolePermission rolePermission) {
        this.rolePermission = rolePermission;
    }
}
