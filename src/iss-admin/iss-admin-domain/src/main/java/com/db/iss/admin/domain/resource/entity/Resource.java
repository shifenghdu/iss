package com.db.iss.admin.domain.resource.entity;

import com.db.iss.admin.domain.common.AbstractEntity;

import javax.persistence.*;

/**
 * Created by andy on 16/7/11.
 * @author andy.shif
 * 资源
 */
@Entity
@Table(name = "admin_resource")
public class Resource extends AbstractEntity {

    public static final Integer RESOURCE_MENU = 0;

    /**
     * 资源名称
     */
    @Column(name = "name",length = 20)
    private String resourceName;

    /**
     * 资源路径
     */
    @Column(name = "path",length = 256)
    private String path;

    /**
     * 资源类型
     */
    @Column(name = "type",length = 11)
    private Integer type;

    /**
     * 父资源
     */
    @ManyToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Resource parent;

    /**
     * 父资源id
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 资源级别
     */
    @Column(name = "level",length = 11)
    private Integer level;


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}


