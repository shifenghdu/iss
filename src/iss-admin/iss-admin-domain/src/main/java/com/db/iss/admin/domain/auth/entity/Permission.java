package com.db.iss.admin.domain.auth.entity;

import com.db.iss.admin.domain.common.AbstractEntity;
import com.db.iss.admin.domain.resource.entity.Resource;
import org.hibernate.annotations.*;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 权限
 */
@Entity
@Table(name = "ADMIN_PERMISSION")
public class Permission extends AbstractEntity {

    /**
     * 权限名称
     */
    @Column(name = "NAME",length = 50)
    private String permissionName;

    /**
     * 权限表达式
     */
    @Column(name = "EXPRESSION",length = 128)
    private String expression;

    /**
     * 角色权限关系
     */
    @Transient
    private RolePermission rolePermission;

    /**
     * 资源ID
     */
    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    /**
     * 资源
     */
    @Transient
    private Resource resource;

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public RolePermission getRolePermission() {
        return rolePermission;
    }

    public void setRolePermission(RolePermission rolePermission) {
        this.rolePermission = rolePermission;
    }
}
