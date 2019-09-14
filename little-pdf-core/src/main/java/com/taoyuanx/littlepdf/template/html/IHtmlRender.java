package com.taoyuanx.littlepdf.template.html;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/821:43
 * @desc: html渲染接口
 */
public interface IHtmlRender {
    /**
     * html渲染接口
     * @param templatePath 模板路径
     * @param renderData 渲染数据
     * @return
     */
    String render(String templatePath, Map<String,Object> renderData);

    /**
     * 是否支持该模板
     * @param templateSuffix
     * @return
     */
    boolean accpect(String templateSuffix);
}
