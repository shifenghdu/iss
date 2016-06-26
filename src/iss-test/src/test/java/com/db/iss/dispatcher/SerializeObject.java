package com.db.iss.dispatcher;

import com.db.iss.core.plugin.EsbMsg;

/**
 * Created by apple on 16/6/26.
 */
public class SerializeObject {

    private Object object;

    private String string = "123";

    private int s1 = 1;

    private Integer integer = 2;


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getS1() {
        return s1;
    }

    public void setS1(int s1) {
        this.s1 = s1;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
