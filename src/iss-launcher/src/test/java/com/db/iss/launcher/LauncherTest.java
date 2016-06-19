package com.db.iss.launcher;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by andy on 2016/6/19.
 */
public class LauncherTest {

    private final String classPath = LauncherTest.class.getClassLoader().getResource(".").getPath() + "/../../../../workspace/lib";

    @Test
    public void launcher(){
        System.out.println(classPath);
        System.setProperty("iss.path",LauncherTest.class.getClassLoader().getResource(".").getPath() + "/../../../../workspace");
        System.setProperty("iss.config","test.xml");
        Main main = new Main().run(new String[]{classPath});
    }

}
