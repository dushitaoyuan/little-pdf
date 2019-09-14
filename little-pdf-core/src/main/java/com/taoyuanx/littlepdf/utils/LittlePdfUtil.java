package com.taoyuanx.littlepdf.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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


}
