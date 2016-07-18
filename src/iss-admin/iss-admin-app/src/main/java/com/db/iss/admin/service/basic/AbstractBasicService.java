package com.db.iss.admin.service.basic;

import com.db.iss.admin.domain.basic.Resource;
import com.db.iss.admin.domain.basic.User;
import com.db.iss.admin.service.basic.vo.AbstractBasicVo;
import com.db.iss.admin.service.basic.vo.LoginUserVo;
import com.db.iss.admin.service.basic.vo.MenuVo;

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
    protected void buildMenus(LoginUserVo loginUserVo, Long parentId){
        User user = User.getUserByName(loginUserVo.getUserName());
        List<Resource> resources = user.getMenuByLevel(parentId);
        basicVo.setMenus(MenuVo.formResources(resources));
    }

    /**
     * 构建菜单层级线
     * @param id
     */
    protected void buildMenuLine(Long id){
        basicVo.setMenuLine(MenuVo.formResources(Resource.getParentLine(id)));
    }

}
