package com.taoyuanx.littlepdf.utils;

import java.io.File;

/**
 * @author dushitaoyuan
 * @date 2019/9/822:34
 * @desc: 文件工具
 */
public class FileUtil {
    public static String getFileSuffix(String filePath) {
        int flag_index = filePath.lastIndexOf(".");
        return filePath.substring(flag_index + 1);
    }

    public static boolean deleteQuietly(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(getFileSuffix("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\ARIALUNI.TTF"));
    }
}
