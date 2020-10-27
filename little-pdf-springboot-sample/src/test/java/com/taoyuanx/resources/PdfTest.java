package com.taoyuanx.resources;

import com.taoyuanx.littlepdf.template.word.OfficePdfUtil;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.junit.Test;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author dushitaoyuan
 * @date 2020/10/26
 */
public class PdfTest {
    @Test
    public void html2PdfTest() throws Exception {
        InputStream html=PdfTest.class.getClassLoader().getResourceAsStream("pdftemplate/html_pdf.html");
        String pdf="d://12.pdf";
        OfficePdfUtil.toPdf(html,DefaultDocumentFormatRegistry.HTML,new FileOutputStream(pdf));


    }


}
