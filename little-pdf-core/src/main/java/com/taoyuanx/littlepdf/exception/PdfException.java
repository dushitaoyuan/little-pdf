package com.taoyuanx.littlepdf.exception;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:04
 * @desc: pdf异常
 */
public class PdfException extends  RuntimeException {
    public PdfException() {
    }

    public PdfException(String message) {
        super(message);
    }

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfException(Throwable cause) {
        super(cause);
    }
}
