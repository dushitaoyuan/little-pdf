package com.taoyuanx.littlepdf.template.html.impl;

import com.taoyuanx.littlepdf.exception.PdfException;
import com.taoyuanx.littlepdf.template.html.IHtmlRender;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/1411:20
 */
public abstract class AbstractHtmlRender implements IHtmlRender {
    protected String suffix;

    @Override
    public abstract String render(String templatePath, Map<String, Object> renderData);

    @Override
    public boolean accpect(String templateSuffix) {
        if (suffix == null) {
            throw new PdfException("htmlrender suffix 未设置");
        }
        return suffix.equalsIgnoreCase(templateSuffix);
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
