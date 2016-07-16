package com.db.iss.admin.domain.basic;

import com.db.iss.admin.domain.common.AbstractEntity;

import javax.persistence.*;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
