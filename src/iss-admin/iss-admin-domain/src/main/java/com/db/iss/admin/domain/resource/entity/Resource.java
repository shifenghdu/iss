package com.db.iss.admin.domain.resource.entity;

import com.db.iss.admin.domain.auth.entity.Permission;
import com.db.iss.admin.domain.common.AbstractEntity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by andy on 16/7/11.
 * @author andy.shif
 * 资源
 */
@Entity
@Table(name = "ADMIN_RESOURCE")
public class Resource extends AbstractEntity {

    public static final short RESOURCE_MENU = 0;

    /**
     * 资源名称
     */
    @Column(name = "NAME",length = 20)
    private String resourceName;

    /**
     * 资源路径
     */
    @Column(name = "PATH",length = 256)
    private String path;

    /**
     * 资源类型
     */
    @Column(name = "TYPE",length = 11)
    private Short type;

    /**
     * 父资源
     */
    @Transient
    private Resource parent;

    /**
     * 父资源id
     */
    @Column(name = "PARENT_ID")
    private Long parentId;

    /**
     * 资源级别
     */
    @Column(name = "LEVEL",length = 11)
    private Short level;


    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}


