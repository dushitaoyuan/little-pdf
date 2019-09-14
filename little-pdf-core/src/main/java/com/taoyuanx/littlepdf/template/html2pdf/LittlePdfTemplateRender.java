package com.taoyuanx.littlepdf.template.html2pdf;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:07
 * @desc: pdf模板接口
 */
public interface LittlePdfTemplateRender {
    void render(String template, Map<String, Object> renderData, OutputStream outputStream);
}
