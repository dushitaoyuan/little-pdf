package com.taoyuanx.resources;

import com.taoyuanx.littlepdf.sign.Itext5PdfSign;
import com.taoyuanx.littlepdf.template.word.OfficePdfUtil;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author dushitaoyuan
 * @date 2020/10/26
 */
public class PdfTest {
    @Test
    public void html2PdfTest() throws Exception {
        InputStream html = PdfTest.class.getClassLoader().getResourceAsStream("pdftemplate/html_pdf.html");
        String pdf = "d://12.pdf";
        OfficePdfUtil.toPdf(html, DefaultDocumentFormatRegistry.HTML, new FileOutputStream(pdf));


    }

    private Itext5PdfSign itext5PdfSign;

    @Test
    public void signTest() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("sign1");
        itext5PdfSign.sign(new FileInputStream("d://temp/demo3.pdf"), new FileOutputStream("d://temp/demo3_signed.pdf"));
        stopWatch.stop();
        stopWatch.start("sign2");
        itext5PdfSign.sign(new FileInputStream("d://temp/demo1.pdf"), new FileOutputStream("d://temp/demo1_signed.pdf"));
        stopWatch.stop();
        stopWatch.start("sign3");
        itext5PdfSign.sign(new FileInputStream("d://temp/demo2.pdf"), new FileOutputStream("d://temp/demo2_signed.pdf"));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    public void signbigTest() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("signBig");
        itext5PdfSign.sign(new FileInputStream("d://temp/big.pdf"), new FileOutputStream("d://temp/big_signed.pdf"));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    @Before
    public void before() {
        String signername = "桃源科技有限公司";
        String reason = "官方承认，不可篡改";
        String location = "桃源科技有限公司";
        String password = "123456";
        String p12Path = "g://data/client.p12";
        String chapterPath = "g://data/stamp.png";
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

}
