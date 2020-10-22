# little-pdf
## 项目介绍

littlepdf 是一个基于数据+模板->html->pdf的小型框架,html渲染模板用户可自行配置,内置thymeleaf,freemarker,word三种实现,实现 IRender接口即可

使用方式参见:little-pdf-springboot-sample,html渲染成pdf基于itext5+flying-saucer(css1.0,css2.0 支持)

基础模板:

html+css ->pdf  

word(docx)->docx->pdf  

word ->pdf 依赖jodconverter ->apache openoffice或者libreoffice 



## 适用场景

适合简单pdf生成,如各种电子合同(租房,网贷,借款等),要求严苛的pdf生成并不适合,渲染pdf过程中,支持多种资源加载方式:
1. 网络
2. classpath
3. 文件系统
具体参见:TemplateLoaderUtil

## 使用详解

###  pdf生成使用

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
    public LittlePdfTemplateRender littlePdfTemplateRender() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("pdftemplate/");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        WordTemplateRender wordTemplateRender = new WordTemplateRender("d://temp/");
        wordTemplateRender.setSuffix("docx");
        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = littlePdfConfig();
        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(thymeleafHtmlRender);
        thymeleafRender.addRender(wordTemplateRender);
        return thymeleafRender;
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
### pdf 关键词签名使用
```java
 @Test
    public  void signTest() throws Exception {
        itext5PdfSign.sign(new FileInputStream("d://temp/word.pdf"),new FileOutputStream("d://temp/word_signed.pdf"));
    }
    @Before
    public void before() {

        String signername = "桃源科技有限公司";
        String reason = "官方承认，不可篡改";
        String location = "桃源科技有限公司";
        String password = "123456";
        String p12Path =  "g://data/client.p12";
        String chapterPath ="g://data/stamp.png";
        String field_name = "sign_Field";

        Itext5PdfSign.SignConfig signConfig = new Itext5PdfSign.SignConfig();
        signConfig.setSignP12Path(p12Path);
        signConfig.setSignP12Password(password);
        signConfig.setChapterImgPath(chapterPath);
        signConfig.setSignername(signername);
        signConfig.setReason(reason);

        signConfig.setLocation(location);
        signConfig.setSignFiledName(field_name);
        signConfig.setSignKeyWord("桃源科技有限公司");
        itext5PdfSign = new Itext5PdfSign(signConfig);


    }


// pdf 定位 实现 参见 com.taoyuanx.littlepdf.sign.Itext5PdfSign.KeyWordFinder
    
```


- html ->pdf 示例
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/render1.png)


![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/render2.png)


- word ->pdf 示例
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/word.png)
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/word2.png)
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/pdf1.png)
![avatar](https://github.com/dushitaoyuan/little-pdf/blob/master/imgs/pdf2.png)




