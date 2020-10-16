package com.taoyuanx.littlepdf.template.html;

import com.taoyuanx.littlepdf.template.AbstractRender;
import com.taoyuanx.littlepdf.template.IRender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/821:47
 * @desc: Thymeleaf 模板渲染
 */
public class ThymeleafHtmlRender extends AbstractRender implements IRender {

    private TemplateEngine templateEngine;
    private static final String DEFAULT_SUFFIX = "html";

    public ThymeleafHtmlRender(TemplateEngine templateEngine) {
        this.suffix = DEFAULT_SUFFIX;
        this.templateEngine = templateEngine;
    }

    public ThymeleafHtmlRender() {
        this.suffix = DEFAULT_SUFFIX;
        this.templateEngine = new TemplateEngine();
    }

    @Override
    public String render(String templatePath, Map<String, Object> renderData) {
        Context context = new Context();
        context.setVariables(renderData);
        return templateEngine.process(templatePath, context);
    }

}
