package com.taoyuanx.littlepdf.sign;

import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

/**
 * @author dushitaoyuan
 * @desc BC tsaclient 工厂 实现
 * @date 2020/1/3
 */
public class TsaClientBCFactory implements TsaClientFactory {
    private String tsaUrl;

    public TsaClientBCFactory(String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    @Override
    public TSAClient newTSAClient() {
        return new TSAClientBouncyCastle(tsaUrl);
    }
}
