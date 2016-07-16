package com.db.iss.admin.domain.basic;

import com.db.iss.admin.domain.common.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by andy on 16/7/14.
 * @author andy.shif
 * 角色权限关系
 */
@Entity
@Table(name = "ADMIN_ROLE_PERMISSION")
public class RolePermission extends AbstractEntity {

    @Column(name = "ROLE_ID",length = 11)
    private Long roleId;

    @Column(name = "PERMISSION_ID",length = 11)
    private Long permissionId;


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

}
