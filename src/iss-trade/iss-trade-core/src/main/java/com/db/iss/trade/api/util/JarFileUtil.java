package com.db.iss.trade.api.util;

import java.io.*;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * Created by andy on 2015/12/22.
 */
public class JarFileUtil {

    public static void unzip(String file, String dir) {
        long startTime = System.currentTimeMillis();
        try {
            JarInputStream Zin = new JarInputStream(new FileInputStream(file));//输入源zip路径
            BufferedInputStream Bin = new BufferedInputStream(Zin);
            String Parent = dir; //输出路径（文件夹目录）
            File Fout = null;
            ZipEntry entry;
            try {
                while ((entry = Zin.getNextEntry()) != null) {
                    Fout = new File(Parent, entry.getName());
                    if (!Fout.exists()) {
                        if (entry.isDirectory()) {
                            Fout.mkdirs();
                            continue;
                        } else {
                            (new File(Fout.getParent())).mkdirs();
                        }
                    }
                    FileOutputStream out = new FileOutputStream(Fout);
                    BufferedOutputStream Bout = new BufferedOutputStream(out);
                    int b;
                    while ((b = Bin.read()) != -1) {
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                }
                Bin.close();
                Zin.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
    }
}
