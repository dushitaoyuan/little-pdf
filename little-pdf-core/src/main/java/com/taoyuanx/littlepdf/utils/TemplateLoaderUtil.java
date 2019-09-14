package com.taoyuanx.littlepdf.utils;

import com.taoyuanx.littlepdf.exception.PdfException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:19
 * @desc: 模板资源加载
 */
public class TemplateLoaderUtil {

    public static InputStream load(String baseDir, String recourceUrl) {
        try {
            if (recourceUrl.startsWith("classpath:")) {
                return TemplateLoaderUtil.class.getClassLoader().getResourceAsStream(recourceUrl.replaceFirst("classpath:", ""));
            }
            if (recourceUrl.startsWith("http")) {
                URL url = new URL(recourceUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return connection.getInputStream();
                }
            }
            if (recourceUrl.startsWith("file://")) {
                URL url = new URL(recourceUrl);
                return new FileInputStream(url.getFile());
            }
            if (recourceUrl.startsWith("/")) {
                return new FileInputStream(recourceUrl);
            } else {
                if(LittlePdfUtil.isNotEmpty(baseDir)&&baseDir.startsWith("classpath:")){
                    baseDir=baseDir.replaceFirst("classpath:", "");
                    if(recourceUrl.endsWith("/")){
                        recourceUrl=baseDir+recourceUrl;
                    }else {
                        recourceUrl=baseDir+"/"+recourceUrl;
                    }
                    return TemplateLoaderUtil.class.getClassLoader().getResourceAsStream(recourceUrl);

                }
                return new FileInputStream(new File(baseDir, recourceUrl));
            }
        } catch (Exception e) {
            throw new PdfException("resources load exception please check recourceUrl :" + recourceUrl, e);
        }
    }

}
