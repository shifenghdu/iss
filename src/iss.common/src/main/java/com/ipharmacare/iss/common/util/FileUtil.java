package com.ipharmacare.iss.common.util;

import java.io.File;

/**
 * Created by andy on 2015/12/22.
 */
public class FileUtil {

    public static void removeDir(String filepath) {
        File f = new File(filepath);//定义文件路径
        if (f.exists() && f.isDirectory()) {//判断是文件还是目录
            if (f.listFiles().length == 0) {//若目录下没有文件则直接删除
                f.delete();
            } else {//若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        removeDir(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();//删除文件
                }
            }
        }
    }
}
