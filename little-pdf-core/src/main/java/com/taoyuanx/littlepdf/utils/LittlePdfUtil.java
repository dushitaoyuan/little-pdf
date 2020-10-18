package com.taoyuanx.littlepdf.utils;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.functions.T;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:37
 * @desc: littlepdf工具类
 */
public class LittlePdfUtil {
    public static String streamToString(InputStream inputStream, String encoding) throws IOException {
        return IOUtils.toString(inputStream, encoding);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.length() >= 0;
    }


    public static byte[] streamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[4 << 1024];
        int len = -1;
        while ((len = inputStream.read(buf)) != -1) {
            outputStream.write(buf, 0, len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public static void removeLastList(List list, Integer num) {
        for (int i = 0; i < num; i++) {
            if (!list.isEmpty()) {
                list.remove(list.size() - 1);
            }
        }
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);

    }

    public static void main(String[] args) {
        System.out.println("123".substring(0, 3));
    }


}
