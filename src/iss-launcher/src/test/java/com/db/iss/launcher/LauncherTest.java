package com.db.iss.launcher;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by andy on 2016/6/19.
 */
public class LauncherTest {

    private final String classPath = LauncherTest.class.getClassLoader().getResource("ctx-app.xml").getPath() + "/../../../../workspace/lib";

    @Test
    public void launcher(){
        System.out.println(classPath);
        Main main = new Main().run(new String[]{classPath});
    }

}
