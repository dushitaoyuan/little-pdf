package com.taoyuanx.littlepdf.sign;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.security.*;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.taoyuanx.littlepdf.utils.RSAUtil;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * pdf 签章实现类
 */
public class Itext5PdfSign {
    private static final Logger LOG = LoggerFactory.getLogger(Itext5PdfSign.class);

    private SignConfig signConfig;

    public void sign(InputStream waitSignPdf, OutputStream signedPdf) {
        try {
            PdfReader reader = new PdfReader(waitSignPdf);
            /**
             * false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
             * true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
             */
            PdfStamper stamper = PdfStamper.createSignature(reader, signedPdf, '\0', null, true);
            /**
             * 设定签章为高清章,默认模糊
             */
            stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
            /**
             * 设定签章属性
             */
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
            appearance.setReason(signConfig.getReason());
            appearance.setLocation(signConfig.getLocation());
            appearance.setSignatureCreator(signConfig.getSignername());
            PdfLocationResult locationResult = calcSignLocation(reader);
            appearance.setVisibleSignature(locationResult.getRectangle(), locationResult.getPageNum(), signConfig.getSignFiledName());
            /**
             * 设定签章图片,签章类别
             */
            appearance.setSignatureGraphic(signConfig.getChapterImg());
            //NOT_CERTIFIED 不会导致pdf上其他签章无效
            appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
            /**
             * 设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
             */
            appearance.setRenderingMode(RenderingMode.GRAPHIC);
            /**
             * 指定摘要算法
             */
            ExternalDigest digest = signConfig.getDigest();
            /**
             * 指定签名对象
             */
            ExternalSignature signature = signConfig.getSignature();
            /**
             * 构造时间时间戳服务器
             */
            TSAClient tsaClient = getTsaClient();
            /**
             * itext 签章
             */
            MakeSignature.signDetached(appearance, digest, signature, signConfig.getChain(), null, null, tsaClient, 0, CryptoStandard.CMS);
        } catch (Exception e) {
            throw new RuntimeException("签章异常", e);
        }
    }


    public Itext5PdfSign(SignConfig signConfig) {
        checkInit(signConfig);
        try {
            BouncyCastleProvider bc = new BouncyCastleProvider();
            Security.addProvider(bc);
            if (StringUtils.isNotEmpty(signConfig.getSignP12Path())) {
                String p12Path = signConfig.getSignP12Path();
                String password = signConfig.getSignP12Password();
                KeyStore ks = RSAUtil.getKeyStore(p12Path, password);
                String alias = ks.aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
                signConfig.setChain(ks.getCertificateChain(alias));
                signConfig.setPrivateKey(privateKey);
            }
            signConfig.setDigest(new BouncyCastleDigest());
            signConfig.setSignature(new PrivateKeySignature(signConfig.getPrivateKey(), DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME));
            if (Objects.isNull(signConfig.getChapterImg())) {
                signConfig.setChapterImg(Image.getInstance(signConfig.getChapterImgPath()));
            }
            Image chapterImg = signConfig.getChapterImg();
            signConfig.setStampWidth(chapterImg.getWidth());
            signConfig.setStampHeight(chapterImg.getHeight());
            this.signConfig = signConfig;
        } catch (Exception e) {
            throw new RuntimeException("init error", e);
        }
    }

    private void checkInit(SignConfig signConfig) {
        if (StringUtils.isEmpty(signConfig.getSignP12Path())) {
            throw new RuntimeException("缺少签章用的公私钥对信息");
        }
        if (StringUtils.isNotEmpty(signConfig.getSignP12Path()) && StringUtils.isEmpty(signConfig.getSignP12Password())) {
            throw new RuntimeException("签章证书密码配置缺少");
        }
        if (Objects.isNull(signConfig.getChapterImg()) && StringUtils.isEmpty(signConfig.getChapterImgPath())) {
            throw new RuntimeException("缺少签章图片地址信息");
        }
        if (StringUtils.isEmpty(signConfig.getSignername())) {
            throw new RuntimeException("缺少签章者名称信息");
        }
        if (StringUtils.isEmpty(signConfig.getReason())) {
            throw new RuntimeException("缺少签章原因信息");
        }
        if (StringUtils.isEmpty(signConfig.getLocation())) {
            throw new RuntimeException("缺少签章位置信息");
        }
        if (StringUtils.isEmpty(signConfig.getSignFiledName())) {
            throw new RuntimeException("缺少签章域名称信息");
        }
        if (Objects.nonNull(signConfig.getTsaClientFactory())) {
            signConfig.setNoTsa(false);
        }
        if (Objects.nonNull(signConfig.getTsaUrl())) {
            signConfig.setNoTsa(false);
            signConfig.tsaClientFactory = new TsaClientBCFactory(signConfig.getTsaUrl());
        }
    }

    private TSAClient getTsaClient() {
        if (signConfig.isNoTsa()) {
            return null;
        }
        return signConfig.tsaClientFactory.newTSAClient();

    }

    @Data
    public static class SignConfig {
        /**
         * 签章hash 及签名实现
         */
        private ExternalDigest digest;
        private ExternalSignature signature;
        /**
         * 证书链及私钥
         */
        private Certificate[] chain;
        private PrivateKey privateKey;
        /**
         * 签章原因
         */
        private String reason;
        /**
         * 签章位置
         */
        private String location;
        /**
         * 签章者名称
         */
        private String signername;
        /**
         * 签章图片地址
         */
        private Image chapterImg;
        private String chapterImgPath;
        /**
         * 签章域名称可根据域查找章的位置
         */
        private String signFiledName;

        /**
         * 签章公私钥文件
         */
        private String signP12Path;
        private String signP12Password;
        /**
         * 签章关键词及关键词所在的页码
         */
        private String signKeyWord;
        private Integer signKeyWordPageNum;
        /**
         * 签章图片的宽高
         */
        private float stampHeight;
        private float stampWidth;

        /**
         * 签章时间服务
         */
        private boolean noTsa = true;
        private TsaClientFactory tsaClientFactory;
        private String tsaUrl;

    }


    private PdfLocationResult calcSignLocation(PdfReader reader) {
        /**
         * 1. 如果关键字存在,则签章在关键字上
         * 2. 如果关键字不存在 则签章在尾页的右下角
         */
        List<KeyWordLocation> keyWordLocationList = keyWordLocation(reader, signConfig.getSignKeyWord(), signConfig.getSignKeyWordPageNum());
        if (!keyWordLocationList.isEmpty()) {
            /**
             * 计算规则:
             * 1. 由于获取的关键词文件块宽高过于夸张,所以先计算关键词文本块矩形左下角坐标( keyWordLocation.getUrx(),keyWordLocation.getUry()-文本块高度)
             * 2. 然后将 文本块左下角坐标作为签章矩形的中心点,然后计算签章域的左小角右上角坐标
             */
            KeyWordLocation keyWordLocation = keyWordLocationList.get(0);

            float keyWordTextBlockHeight = keyWordLocation.getKeyWordTextBlockHeight();


            //右上角坐标
            float urx = keyWordLocation.getUrx() + signConfig.getStampWidth() / 2;
            float ury = keyWordLocation.getUry() - keyWordTextBlockHeight + signConfig.getStampHeight() / 2;
            //左下角坐标
            float llx = urx - signConfig.getStampWidth();
            float lly = ury - signConfig.getStampHeight();
            Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
            LOG.debug("查找到关键词[{}]位置,坐标为", keyWordLocation.getText(), keyWordLocation);
            return new PdfLocationResult(keyWordLocation.getPageNum(), rectangle);
        } else {
            int numberOfPages = reader.getNumberOfPages();
            Rectangle lastPageSize = reader.getPageSize(numberOfPages);
            float pageWidth = lastPageSize.getWidth();
            //左下角坐标 lly 表示距离底部的距离
            float llx = pageWidth - signConfig.getStampWidth();
            float lly = 50;
            //右上角坐标
            float urx = llx + signConfig.getStampWidth();
            float ury = lly + signConfig.getStampHeight();
            Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
            LOG.debug("未查找到关键词位置,签章位置默认在尾页", signConfig.getSignKeyWord());
            return new PdfLocationResult(numberOfPages, rectangle);
        }

    }

    /**
     * 根据关键词 计算签章位置
     *
     * @param pdfReader      解析reader
     * @param keyWord        关键词
     * @param keyWordPageNum 关键词所在页码
     * @return
     */
    private List<KeyWordLocation> keyWordLocation(PdfReader pdfReader, String keyWord, Integer keyWordPageNum) {
        List<KeyWordLocation> keyWordLocationList = Lists.newArrayList();
        try {
            int pageSize = pdfReader.getNumberOfPages();
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
            IntStream pageStream = null;
            if (Objects.nonNull(keyWordPageNum)) {
                pageStream = IntStream.of(keyWordPageNum);
            } else {
                pageStream = IntStream.range(1, pageSize + 1);
            }
            pageStream.forEach(pageNum -> {
                try {
                    pdfReaderContentParser.processContent(pageNum, new TextMarginFinder() {
                        @Override
                        public void renderText(TextRenderInfo renderInfo) {
                            super.renderText(renderInfo);
                            String text = renderInfo.getText();
                            //查找到关键词,并设置关键词位置
                            if (Objects.nonNull(text) && text.contains(keyWord)) {
                                KeyWordLocation keyWordLocation = new KeyWordLocation();
                                keyWordLocation.setKeyWordTextBlockHeight(this.getHeight());
                                keyWordLocation.setKeyWordTextBlockWidth(this.getWidth());
                                keyWordLocation.setLlx(this.getLlx());
                                keyWordLocation.setLly(this.getLly());
                                keyWordLocation.setUrx(this.getUrx());
                                keyWordLocation.setUry(this.getUry());
                                keyWordLocation.setText(text);
                                keyWordLocation.setPageNum(pageNum);
                                keyWordLocationList.add(keyWordLocation);
                            }
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyWordLocationList;
    }

    /**
     * 签章关键词文本位置
     */
    @Data
    public static class KeyWordLocation {
        /**
         * 文本
         */
        private String text;
        /**
         * 关键词所在pdf页码
         */
        private Integer pageNum;
        /**
         * 文本块所在矩形左下角坐标
         */
        private float llx;
        private float lly;
        /**
         * 文本块所在矩形右上角角坐标
         */
        private float urx;
        private float ury;
        /**
         * 文本块宽高
         */
        private float keyWordTextBlockWidth;
        private float keyWordTextBlockHeight;

    }

    @Data
    public static class PdfLocationResult {
        /**
         * 签章所在pdf页码
         */
        private int pageNum;
        /**
         * 签章矩形
         */
        private Rectangle rectangle;

        public PdfLocationResult(int pageNum, Rectangle rectangle) {
            this.pageNum = pageNum;
            this.rectangle = rectangle;
        }
    }
}
