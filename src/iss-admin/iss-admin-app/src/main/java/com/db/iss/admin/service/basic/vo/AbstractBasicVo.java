package com.db.iss.admin.service.basic.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * basic Vo基类
 */
public abstract class AbstractBasicVo implements Serializable {

    /**
     * 显示菜单
     */
    protected List<MenuVo> menus;

    /**
     * 当前菜单层级关系
     */
    protected List<MenuVo> menuLine;


    public List<MenuVo> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuVo> menus) {
        this.menus = menus;
    }


    public List<MenuVo> getMenuLine() {
        return menuLine;
    }

    public void setMenuLine(List<MenuVo> menuLine) {
        this.menuLine = menuLine;
    }
}
