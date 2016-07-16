package com.db.iss.admin.domain.basic;

import com.db.iss.admin.domain.common.AbstractEntity;
import com.db.iss.admin.domain.common.Repository;
import com.db.iss.admin.domain.common.RepositoryProvider;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/7/2.
 * @author andy.shif
 * 用户
 */
@Entity
@Table(name = "ADMIN_USER")
public class User extends AbstractEntity {

    @Column(name = "NAME",length = 20, unique = true)
    private String userName;

    @Column(name = "PASSWORD",length = 64)
    private String password;

    @Transient
    private List<Role> roles;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    private static final String SELECT_PERMISSIONS_BY_USER_ID = "select a from Permission a,RolePermission b,UserRole c "+
            "where a.id = b.permissionId and b.roleId = c.roleId and c.userId = ?";

    /**
     * 获取用户权限
     * @return
     */
    public List<Permission> getPermissions(){
        return getRepository().queryForList(SELECT_PERMISSIONS_BY_USER_ID,getId());
    }

    private static final String SELECT_ROLES_BY_USER_ID = "select a form Role a,UserRole b " +
            "where a.id = b.roleId and b.userId = ?";

    /**
     * 获取用户角色
     * @return
     */
    public List<Role> getRolesByUserId(){
        return getRepository().queryForList(SELECT_ROLES_BY_USER_ID,getId());
    }

    /**
     * 通过用户名获取用户
     * @param userName
     * @return
     */
    public static User getUserByName(String userName){
        Repository repository = RepositoryProvider.getRepository(Repository.class);
        return (User) repository.query("from User a where a.userName = ?",userName);
    }

    /**
     * 获取有权限的指定菜单的子菜单
     * @return
     */
    public List<Resource> getMenuByLevel(Long parentId){
        List<Permission> permissions = getPermissions();
        List<Resource> resources = Resource.getMenuResourceByParent(parentId);
        List<Resource> result = new ArrayList<>();
        if(resources != null){
            for(Resource resource : resources){
                for(Permission permission : permissions) {
                    if (resource.getId() == permission.getResourceId()){
                        result.add(resource);
                    }
                }
            }
        }
        return result;
    }
}
