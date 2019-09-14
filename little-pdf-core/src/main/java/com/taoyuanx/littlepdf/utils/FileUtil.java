package com.taoyuanx.littlepdf.utils;

/**
 * @author dushitaoyuan
 * @date 2019/9/822:34
 * @desc: 文件工具
 */
public class FileUtil {
    public  static  String getFileSuffix(String filePath){
        int flag_index = filePath.lastIndexOf(".");
        return  filePath.substring(flag_index+1);
    }

    public static void main(String[] args) {
        System.out.println(getFileSuffix("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\ARIALUNI.TTF"));
    }
}
