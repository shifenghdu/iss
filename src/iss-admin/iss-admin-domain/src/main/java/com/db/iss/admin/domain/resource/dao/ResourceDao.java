package com.db.iss.admin.domain.resource.dao;

import com.db.iss.admin.domain.auth.entity.Permission;
import com.db.iss.admin.domain.auth.entity.User;
import com.db.iss.admin.domain.common.AbstractDao;
import com.db.iss.admin.domain.resource.entity.Resource;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/7/11.
 * @author andy.shif
 * 资源dao
 */
@Repository
public class ResourceDao extends AbstractDao<Resource> {

    /**
     * 获取用户第一层菜单
     * @param userId
     * @return
     */
    @Transactional
    public List<Resource> getFirstMenuResourceByUser(Long userId){
        Query query = getCurrentSession()
                .createQuery("select a from Permission a, RolePermission b, UserRole c "
                + "where c.userId = :userId and c.roleId = b.roleId" +
                        " and b.permissionId = a.id")
                .setParameter("userId",userId);
        List<Permission> permissions = query.list();
        query = getCurrentSession().createQuery("from Resource as r where r.type = :type " +
                "and r.level = 0").setParameter("type",Resource.RESOURCE_MENU);
        List<Resource> resources = query.list();
        ArrayList<Resource> result = new ArrayList<>();

        for(Resource resource : resources){
            for(Permission permission : permissions) {
                if (permission.getResourceId() == resource.getId().intValue()) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

}
