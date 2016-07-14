package com.db.iss.admin.domain.resource;

import com.db.iss.admin.domain.resource.dao.ResourceDao;
import com.db.iss.admin.domain.resource.entity.Resource;
import org.hibernate.annotations.Any;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by andy on 16/7/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ctx-domain.xml")
public class ResourceTests {

    @Autowired
    private ResourceDao dao;

    @Test
    public void getFirstMenuResourceByUserTestCase(){
        List<Resource> resourceList = dao.getFirstMenuResourceByUser(1L);
    }


}
