package com.taoyuanx.littlepdf.template.html2pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author dushitaoyuan
 * @desc 用途描述
 * @date 2020/10/16
 */
public class Html2PdfUtil {
    public static void html2Pdf(String html, OutputStream out,Itext5PdfRenderConfig renderConfig) throws Exception {
        Charset charset = renderConfig.getCharset() == null ? Charset.defaultCharset() : Charset.forName(renderConfig.getCharset());
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        // 字体处理
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(new CssAppliersImpl(renderConfig.getFontProvider()));
        // img图片加载
        htmlContext.setImageProvider(renderConfig.getImageProvider());
        htmlContext.setAcceptUnknown(true).autoBookmark(true).setTagFactory(Tags.getHtmlTagProcessorFactory());
        // css加载
        CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
        cssResolver.setFileRetrieve(renderConfig.getFileRetrieve());

        HtmlPipeline htmlPipeline = new HtmlPipeline(htmlContext, new PdfWriterPipeline(document, writer));
        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser parser = new XMLParser(true, worker, charset);
        parser.parse(new ByteArrayInputStream(html.getBytes()), charset);
        document.close();
    }
}
