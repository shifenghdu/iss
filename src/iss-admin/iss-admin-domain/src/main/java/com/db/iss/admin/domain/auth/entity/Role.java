package com.db.iss.admin.domain.auth.entity;

import com.db.iss.admin.domain.common.AbstractEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Set;

/**
 * Created by andy on 16/7/9.
 * @author andy.shif
 * 角色
 */
@Entity
@Table(name = "admin_role")
public class Role extends AbstractEntity {

    @Column(name = "name",length = 20)
    private String roleName;

    @OneToMany(cascade = CascadeType.PERSIST,targetEntity = User.class,
            fetch = FetchType.LAZY,mappedBy = "roles")
    private Set<Permission> permissions;

    @ManyToMany(cascade = CascadeType.PERSIST,targetEntity = Permission.class,
            fetch = FetchType.LAZY,mappedBy = "role")
    private Set<User> users;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
