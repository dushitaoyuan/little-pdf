package com.taoyuanx.littlepdf.template.html.impl;

import com.taoyuanx.littlepdf.template.html.IHtmlRender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/821:47
 * @desc:
 */
public class TemplateHtmlRender extends AbstractHtmlRender implements IHtmlRender {

    private TemplateEngine templateEngine;
    private  static  final String DEFAULT_SUFFIX="html";
    public TemplateHtmlRender(TemplateEngine templateEngine) {
        this.suffix=DEFAULT_SUFFIX;
        this.templateEngine = templateEngine;
    }

    public TemplateHtmlRender() {
        this.suffix=DEFAULT_SUFFIX;
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public String render(String templatePath, Map<String, Object> renderData) {
        Context context = new Context();
        context.setVariables(renderData);
        return templateEngine.process(templatePath, context);
    }

}
