package com.ulearning.ulms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author chenyu
 * @desc 文件工具类
 * @datetime 2021年11月10日 10:36
 * Copyright (c) 2006-2021.Beijing WenHua Online Sci-Tech Development Co. Ltd
 * All rights reserved.
 */
public class FileUtil {

    public static void copy(File source, File fil) throws IOException {
        // 判断源目录是不是一个目录
        if(!source.isDirectory()){
            //如果不是目录就不复制
            return;
        }
        //创建目标目录的file对象
        if (!fil.exists()) {
            //不存在就创建文件夹
            fil.mkdir();
        } else {
            return;
        }
        //如果源文件存在就复制
        if (source.exists()) {
            // 获取源目录下的File对象列表
            File[] files = source.listFiles();

            for (File file2 : files) {
                //新文件夹的路径
                File file4 = new File(fil + File.separator + file2.getName());

                if (file2.isDirectory()) {
                    copy(file2, file4);
                }
                if (file2.isFile()) {
                    FileInputStream in = new FileInputStream(file2);
                    FileOutputStream out = new FileOutputStream(file4);

                    byte[] bs = new byte[1026];

                    int count = 0;
                    //循环把源文件的内容写入新文件
                    while ((count = in.read(bs, 0, bs.length)) != -1) {
                        out.write(bs, 0, count);
                    }
                    //关闭流
                    out.flush();
                    out.close();
                    in.close();
                }
            }
        }
    }

    /**
     * 递归复制文件夹及其内容
     *
     * @param source 源文件夹路径
     * @param target 目标文件夹路径
     * @throws IOException 如果复制过程中发生错误
     */
    public static void copyDirectory(Path source, Path target) throws IOException {
        // 如果源路径不存在，抛出异常
        if (!Files.exists(source)) {
            throw new IOException("源文件夹不存在: " + source);
        }

        // 如果目标路径不存在，创建目标文件夹
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        // 遍历源文件夹中的所有文件和子文件夹
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path file : stream) {
                Path destination = target.resolve(source.relativize(file));
                if (Files.isDirectory(file)) {
                    // 如果是文件夹，递归复制
                    copyDirectory(file, destination);
                } else {
                    // 如果是文件，直接复制
                    Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

}
