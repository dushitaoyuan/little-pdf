package com.taoyuanx.littlepdf.template.word;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.*;

/**
 * @author dushitaoyuan
 * @desc word转pdf
 * @date 2020/10/15
 * 环境依赖:
 * Apache OpenOffice https://www.openoffice.org/download/index.html
 * 或 LibreOffice    https://www.libreoffice.org/
 */

public class OfficePdfUtil {
    public static void toPdf(InputStream wordPath,DocumentFormat src, OutputStream pdfOutputStream ) throws Exception {
        final LocalOfficeManager officeManager = LocalOfficeManager.install();
        try {
            officeManager.start();
            JodConverter
                    .convert(wordPath)
                    .as(src)
                    .to(pdfOutputStream)
                    .as(DefaultDocumentFormatRegistry.PDF)
                    .execute();
        } finally {
            OfficeUtils.stopQuietly(officeManager);
            wordPath.close();
            pdfOutputStream.close();
        }
    }
}
