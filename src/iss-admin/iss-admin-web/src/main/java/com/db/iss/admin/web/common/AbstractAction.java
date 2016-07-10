package com.db.iss.admin.web.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by andy on 16/7/10.
 * @author andy.shif
 * 公用抽象action
 */
public abstract class AbstractAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }

}
