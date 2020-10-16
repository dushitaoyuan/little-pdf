package com.taoyuanx.littlepdf.sign;

import com.itextpdf.text.pdf.security.TSAClient;

/**
 * @author dushitaoyuan
 * @desc tsaclient 工厂
 * @date 2020/1/3
 */
public interface TsaClientFactory {
     TSAClient newTSAClient();
}
