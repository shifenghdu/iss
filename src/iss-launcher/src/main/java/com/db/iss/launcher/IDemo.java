package com.db.iss.launcher;

import com.db.iss.core.plugin.EsbMsg;
import com.db.iss.dispatcher.annotation.Remote;

/**
 * Created by apple on 16/6/25.
 */
@Remote(version = "v1.0.0",author = "andy",describe = "测试服务")
public interface IDemo {

    String hello(String name);

    EsbMsg call(int a, int b, String s);

    void get();

//    String a1(String name);
//
//    String a2(String name);
//    String a3(String name);
//    String a4(String name);
//    String a5(String name);
//    String a6(String name);
//    String a7(String name);
//    String a8(String name);
//    String a9(String name);
//    String a10(String name);
//    String a11(String name);
//    String a12(String name);
//    String a13(String name);
//    String a14(String name);
//    String a15(String name);
//    String a16(String name);
//    String a17(String name);
//    String a18(String name);
//    String a19(String name);
//    String a20(String name);








}
