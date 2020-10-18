package com.taoyuanx.littlepdf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public final class RSAUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);


    public static final String KEYSTORE_TYPE_P12 = "PKCS12";
    public static final String KEYSTORE_TYPE_JKS = "JKS";

    public static KeyStore getKeyStore(String filePath, String keyPassword) throws Exception {

        KeyStore keyStore = KeyStore.getInstance(guessKeyStoreType(filePath));
        FileInputStream file = new FileInputStream(new File(filePath));
        keyStore.load(file, keyPassword.toCharArray());
        return keyStore;
    }

    public static KeyStore getKeyStore(InputStream inputStream, String keyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE_P12);
        keyStore.load(inputStream, keyPassword.toCharArray());
        return keyStore;
    }

    public static String guessKeyStoreType(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".")+1);
        if (ext.equals("p12") || ext.equals("pfx")) {
            return KEYSTORE_TYPE_P12;
        }
        if (ext.equals("jks")) {
            return KEYSTORE_TYPE_JKS;
        }
        return null;
    }





}
