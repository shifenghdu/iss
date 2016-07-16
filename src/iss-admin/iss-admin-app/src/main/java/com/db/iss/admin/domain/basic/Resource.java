package com.db.iss.admin.domain.basic;

import com.db.iss.admin.domain.common.AbstractEntity;
import com.db.iss.admin.domain.common.Repository;
import com.db.iss.admin.domain.common.RepositoryProvider;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

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

    @Transient
    private List<Resource> children;

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

    public List<Resource> getChildren() {
        return children;
    }

    public void setChildren(List<Resource> children) {
        this.children = children;
    }

    private static final String SELECT_FIRST_MENU = "from Resource as r where r.type = 0 and r.parentId = ?";

    /**
     * 获取所有指定菜单的子菜单
     * @param parentId
     * @return
     */
    public static List<Resource> getMenuResourceByParent(Long parentId){
        return RepositoryProvider.getRepository(Repository.class).queryForList(SELECT_FIRST_MENU,parentId);
    }
}


