package com.db.iss.admin.domain.resource;

import com.db.iss.admin.domain.basic.Resource;
import com.db.iss.admin.domain.basic.User;
import com.db.iss.admin.service.basic.IHomeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by andy on 16/7/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ctx-app.xml")
public class ResourceTests {

    @Autowired
    private IHomeService basicService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    @Transactional
    public void getFirstMenuResourceByUserTestCase(){
        User user = User.getUserByName("admin");
        List<Resource> resources = user.getMenuByLevel(-1L);
        logger.warn("{}",resources.size());
    }


}
