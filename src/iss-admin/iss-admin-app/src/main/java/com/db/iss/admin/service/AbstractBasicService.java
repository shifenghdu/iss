package com.db.iss.admin.service;

import com.db.iss.admin.domain.basic.Resource;
import com.db.iss.admin.service.vo.AbstractBasicVo;
import com.db.iss.admin.service.vo.MenuVo;

import java.util.List;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * Home主页服务基类
 */
public abstract class AbstractBasicService {

    protected AbstractBasicVo basicVo;


    public AbstractBasicService(AbstractBasicVo basicVo){
        this.basicVo = basicVo;
    }

    public AbstractBasicVo getBasicVo() {
        return basicVo;
    }

    public void setBasicVo(AbstractBasicVo basicVo) {
        this.basicVo = basicVo;
    }

    /**
     * 构建指定菜单的子菜单
     * @param parentId
     */
    protected void buildMenus(Long parentId){
        List<Resource> resources = Resource.getMenuResourceByParent(parentId);
        basicVo.setMenus(MenuVo.formResources(resources));
    }

}
