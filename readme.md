# little-pdf
## 项目介绍

littlepdf 是一个基于数据+模板->html->pdf的小型框架,html渲染模板用户可自行配置,内置thymeleaf,freemarker两种实现,实现 IHtmlRender接口即可
使用方式参见:little-pdf-springboot-sample,html渲染成pdf基于itext5+flying-saucer(css1.0,css2.0 支持)

## 适用场景

适合简单pdf生成,如各种电子合同(租房,网贷,借款等),要求严苛的pdf生成并不适合,渲染pdf过程中,支持多种资源加载方式:
1. 网络
2. classpath
3. 文件系统
具体参见:TemplateLoaderUtil

## 使用详解

```java
1. 配置


@Configuration

public class PdfConfig {
    @ConfigurationProperties(prefix = "little.pdf")
    @Bean
    public Itext5PdfRenderConfig  littlePdfConfig(){
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        return renderConfig;
    }
    @Bean
    @ConditionalOnBean(value = {Itext5PdfRenderConfig.class})
    public LittlePdfTemplateRender  littlePdfTemplateRender(){
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("pdftemplate/");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        TemplateHtmlRender templateHtmlRender = new TemplateHtmlRender(templateEngine);
        templateHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = littlePdfConfig();
        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender(templateHtmlRender);
        return  thymeleafRender;
    }
}

little.pdf.fontsDir=G:\github\little-pdf\little-pdf-springboot-sample\src\main\resources\fonts
little.pdf.resourcesDir=classpath:/pdftemplate
little.pdf.charset=UTF-8

2. 模板渲染

  littlePdfTemplateRender.render(template,jsonObject,outputStreamm);


3.配置解释
参见:com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig

```
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/render1.png)


![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/render2.png)





