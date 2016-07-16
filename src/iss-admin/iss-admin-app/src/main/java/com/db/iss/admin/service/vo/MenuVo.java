package com.db.iss.admin.service.vo;

import com.db.iss.admin.domain.basic.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/7/16.
 * @author andy.shif
 * 菜单 --> Resource
 */
public class MenuVo implements Serializable{

    private Long Id;

    private String name;

    private String url;

    private Short level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    /**
     * domain Resource list 转换成 MenuVo list
     * @param resources
     * @return
     */
    public static List<MenuVo> formResources(List<Resource> resources){
        List<MenuVo> result = new ArrayList<>();
        if(resources != null) {
            for (Resource resource : resources) {
                MenuVo menuVo = new MenuVo();
                menuVo.setName(resource.getResourceName());
                menuVo.setUrl(resource.getPath());
                menuVo.setLevel(resource.getLevel());
                menuVo.setId(resource.getId());
                result.add(menuVo);
            }
        }
        return result;
    }
}
