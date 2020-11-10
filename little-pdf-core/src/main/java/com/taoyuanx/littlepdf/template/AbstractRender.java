package com.taoyuanx.littlepdf.template;

import com.taoyuanx.littlepdf.exception.PdfException;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/1411:20
 */
public abstract class AbstractRender implements IRender {
    protected String suffix;

    @Override
    public abstract String render(String templatePath, Map<String, Object> renderData);

    @Override
    public boolean accept(String templateSuffix) {
        if (suffix == null) {
            throw new PdfException("render suffix 未设置");
        }
        return suffix.equalsIgnoreCase(templateSuffix);
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
