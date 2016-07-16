package com.db.iss.admin.domain.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by andy on 16/7/5.
 * @author andy.shif
 * entity基类
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    /**
     * 公共存储属性
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "last_modify_time")
    private Date lastModifyTime;

    @Column(name = "operator")
    private Long operator;

    /**
     * 实体模型对用仓储(参考DDD模型)
     */
    @Transient
    protected Repository repository;

    /**
     * 初始化
     */
    public AbstractEntity(){
        repository = RepositoryProvider.getRepository(Repository.class);
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }



    public Serializable save(){
        return getRepository().save(this);
    }

    public void delete(){
        getRepository().delete(this);
    }

    public <T> T merge(){
        return (T) getRepository().merge(this);
    }

    public void update(){
        getRepository().update(this);
    }
}
