package com.db.iss.admin.domain.resource.dao;

import com.db.iss.admin.domain.common.AbstractDao;
import com.db.iss.admin.domain.resource.entity.Resource;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by andy on 16/7/11.
 * @author andy.shif
 * 资源dao
 */
@Repository
public class ResourceDao extends AbstractDao<Resource> {

    public List<Resource> getFirstMenuResource(){
        return getCurrentSession().createCriteria(Resource.class)
                .add(Restrictions.eq("type",Resource.RESOURCE_MENU)).list();
    }

}
