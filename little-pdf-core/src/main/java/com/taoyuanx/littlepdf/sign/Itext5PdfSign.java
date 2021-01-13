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
import com.taoyuanx.littlepdf.utils.LittlePdfUtil;
import com.taoyuanx.littlepdf.utils.RSAUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
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
            PdfLocationResult locationResult = calcSignLocation(reader, KeyWordFinder.KeyWordMatchType.MATCH_LAST);
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


    private PdfLocationResult calcSignLocation(PdfReader reader, KeyWordFinder.KeyWordMatchType matchType) {
        /**
         * 1. 如果关键字存在,则签章在关键字上
         * 2. 如果关键字不存在 则签章在尾页的右下角
         */
        KeyWordLocation keyWordLocation = keyWordLocation(reader, signConfig.getSignKeyWord(), signConfig.getSignKeyWordPageNum(), matchType);
        if (Objects.nonNull(keyWordLocation)) {
            /**
             * 计算规则:
             * 1. 由于获取的关键词文件块宽高过于夸张,所以先计算关键词文本块矩形右下角坐标( keyWordLocation.getUrx(),keyWordLocation.getUry()-文本块高度)
             * 2. 然后将 文本块右下角坐标作为签章矩形的中心点,然后计算签章域的左下角右上角坐标
             * 3.签章域超出pdf页宽高,并做溢出处理
             */
            float keyWordTextBlockHeight = keyWordLocation.getKeyWordTextBlockHeight();
            /**
             * 关键字右下角坐标
             */
            float keyWordLrx = keyWordLocation.getUrx();
            float keyWordLry = keyWordLocation.getUry() - keyWordTextBlockHeight;

            //签章域左下角坐标
            float llx = keyWordLrx - signConfig.getStampWidth() / 2;
            float lly = keyWordLry - signConfig.getStampHeight() / 2;
            if (llx < 0) {
                llx = 0;
            }
            /**
             * 横纵坐标向溢出处理
             */
            Rectangle pageSize = reader.getPageSize(keyWordLocation.getPageNum());
            if (llx + signConfig.getStampWidth() > pageSize.getWidth()) {
                llx = pageSize.getWidth() - signConfig.getStampWidth();
            }
            if (lly < 0) {
                lly = 0;
            }
            //签章域右上角坐标
            float urx = llx + signConfig.getStampWidth();
            float ury = lly + signConfig.getStampHeight();


            Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
            LOG.debug("查找到关键词[{}]位置,坐标为{}", keyWordLocation.getText(), keyWordLocation);
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
    private KeyWordLocation keyWordLocation(PdfReader pdfReader, String keyWord, Integer keyWordPageNum, KeyWordFinder.KeyWordMatchType matchType) {
        List<List<KeyWordLocation>> allPageKeyWordLocationList = new CopyOnWriteArrayList<>();
        try {
            int pageSize = pdfReader.getNumberOfPages();
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
            List<Integer> findPageList = null;
            if (Objects.nonNull(keyWordPageNum)) {
                findPageList = new ArrayList<>(keyWordPageNum);
            } else {
                findPageList = IntStream.range(1, pageSize + 1).mapToObj(num -> {
                    return num;
                }).collect(Collectors.toList());
            }

            for (int i = 0, len = findPageList.size(); i < len; i++) {
                try {
                    int pageNum = findPageList.get(i);
                    KeyWordFinder keyWordFinder = new KeyWordFinder(keyWord, pageNum);

                    pdfReaderContentParser.processContent(pageNum, keyWordFinder);
                    List<KeyWordLocation> pageKeyWordLocationList = keyWordFinder.getKeyWordLocationList();
                    if (Objects.nonNull(pageKeyWordLocationList) && !pageKeyWordLocationList.isEmpty()) {
                        if (matchType.equals(KeyWordFinder.KeyWordMatchType.MATCH_FIRST)) {
                            return findKeyWordLocationForPage(pageKeyWordLocationList, keyWord, matchType);
                        } else {
                            allPageKeyWordLocationList.add(pageKeyWordLocationList);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (allPageKeyWordLocationList.isEmpty()) {
            return null;
        }
        if (allPageKeyWordLocationList.size() == 1) {
            return findKeyWordLocationForPage(allPageKeyWordLocationList.get(0), keyWord, matchType);
        }
        return findKeyWordLocationForPage(LittlePdfUtil.getLast(allPageKeyWordLocationList), keyWord, matchType);
    }

    private KeyWordLocation findKeyWordLocationForPage(List<KeyWordLocation> pageKeyWordLocationList, String keyWord, KeyWordFinder.KeyWordMatchType matchType) {
        KeyWordLocation keyWordLocation = null;
        if (pageKeyWordLocationList.isEmpty()) {
            return null;
        }
        if (pageKeyWordLocationList.size() == 1) {
            keyWordLocation = pageKeyWordLocationList.get(0);
        } else {
            switch (matchType) {
                case MATCH_FIRST:
                    keyWordLocation = pageKeyWordLocationList.get(0);
                    break;
                default:
                    keyWordLocation = LittlePdfUtil.getLast(pageKeyWordLocationList);
                    break;
            }
        }
        if (KeyWordFinder.KEYWORD_MATCH_TYPE_FULL.equals(keyWordLocation.getKeywordMatchType())) {
            return keyWordLocation;
        } else if (KeyWordFinder.KEYWORD_MATCH_TYPE_MIX.equals(keyWordLocation.getKeywordMatchType()) &&
                Objects.nonNull(keyWordLocation.getMixMatchCharList()) &&
                Objects.nonNull(keyWordLocation.getAllMatchCount()) && keyWordLocation.getAllMatchCount().equals(keyWord.length())) {
            /**
             * 如果关键词被分拆,则返回中间字符的坐标
             */
            List<KeyWordCharLocation> mixMatchCharList = keyWordLocation.getMixMatchCharList();
            KeyWordLocation midChar = KeyWordFinder.getMidChar(keyWord, mixMatchCharList);
            keyWordLocation.setText(keyWord);
            keyWordLocation.setLlx(midChar.getLlx());
            keyWordLocation.setLly(midChar.getLly());
            keyWordLocation.setUrx(midChar.getUrx());
            keyWordLocation.setUry(midChar.getUry());
            keyWordLocation.setKeyWordTextBlockHeight(midChar.getKeyWordTextBlockHeight());
            keyWordLocation.setKeyWordTextBlockWidth(midChar.getKeyWordTextBlockWidth());
            return keyWordLocation;
        }
        return keyWordLocation;
    }


    public static class KeyWordFinder extends TextMarginFinder {
        @Getter
        @Setter
        private String keyWord;
        @Getter
        @Setter
        private Integer pageNum;
        @Getter
        @Setter
        private List<KeyWordLocation> keyWordLocationList;
        /**
         * 匹配类型
         * <p>
         * KEYWORD_MATCH_TYPE_FULL 关键词完全匹配
         * KEYWORD_MATCH_TYPE_MIX 关键词混乱匹配
         */
        public static final Integer KEYWORD_MATCH_TYPE_FULL = 1,
                KEYWORD_MATCH_TYPE_MIX = 2;
        /**
         * 当前模式 是否为混乱模式
         */
        private boolean isMixMatch;
        /**
         * 混乱匹配模式,已匹配的字符数(左匹配)
         */
        private Integer keyWordMixMatchCharMatchCount = 0;


        public KeyWordFinder(String keyWord, Integer pageNum) {
            this.keyWord = keyWord;
            this.pageNum = pageNum;
            this.keyWordLocationList = new ArrayList<>();
        }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            super.renderText(renderInfo);
            String text = renderInfo.getText();
            LOG.debug("pdf文本:[{}] 文本宽度:{},文本高度:{}", text, this.getWidth(), this.getHeight());
            //查找到关键词,并设置关键词位置
            if (LittlePdfUtil.isNotEmpty(text)) {
                if (isMixMatch) {
                    handleMatchMix(text);
                } else if (text.contains(keyWord)) {
                    keyWordLocationList.add(toKeyWordLocation(text, KEYWORD_MATCH_TYPE_FULL));
                    return;
                } else {
                    handleMatchMix(text);
                    return;
                }
            }


        }


        private void handleMatchMix(String text) {
            int keyWordLength = keyWord.length();
            if (!isMixMatch) {
                MaybeMixMatch mixMatch = isMixMatch(keyWord, text);
                if (mixMatch.isMixMatch()) {
                    isMixMatch = true;
                    keyWordMixMatchCharMatchCount = mixMatch.getMatchCount();
                    KeyWordLocation keyWordMixMatchLocation = toKeyWordLocation(text, KEYWORD_MATCH_TYPE_MIX);
                    keyWordMixMatchLocation.setAllMatchCount(keyWordMixMatchCharMatchCount);
                    KeyWordCharLocation keyWordCharLocation = toKeyWordCharLocation(text);
                    keyWordCharLocation.setMixMatchCount(keyWordMixMatchCharMatchCount);
                    keyWordCharLocation.setMixMatchStart(mixMatch.getMatchStart());
                    List<KeyWordCharLocation> mixMatchCharList = new ArrayList<>();
                    mixMatchCharList.add(keyWordCharLocation);
                    keyWordMixMatchLocation.setMixMatchCharList(mixMatchCharList);
                    keyWordLocationList.add(keyWordMixMatchLocation);
                    if (keyWordMixMatchCharMatchCount.equals(keyWordLength)) {
                        //当已匹配字符数等于关键词字符数 匹配结束
                        isMixMatch = false;
                        keyWordMixMatchCharMatchCount = 0;
                        return;
                    }
                    return;
                }
            } else {
                int matchCount = leftMatch(keyWord, text, keyWordMixMatchCharMatchCount);
                if (matchCount > 0) {
                    KeyWordLocation keyWordMixMatchLocation = LittlePdfUtil.getLast(keyWordLocationList);
                    //继续匹配中
                    List<KeyWordCharLocation> mixMatchCharList = keyWordMixMatchLocation.getMixMatchCharList();
                    KeyWordCharLocation keyWordCharLocation = toKeyWordCharLocation(text);
                    keyWordCharLocation.setMixMatchCount(matchCount);
                    keyWordCharLocation.setMixMatchStart(keyWordMixMatchCharMatchCount);
                    mixMatchCharList.add(keyWordCharLocation);
                    keyWordMixMatchCharMatchCount += matchCount;
                    keyWordMixMatchLocation.setAllMatchCount(keyWordMixMatchCharMatchCount);
                    if (keyWordMixMatchCharMatchCount.equals(keyWordLength)) {
                        //当已匹配字符数等于关键词字符数 匹配结束
                        isMixMatch = false;
                        keyWordMixMatchCharMatchCount = 0;
                        return;
                    }
                } else {
                    //未匹配,删除
                    isMixMatch = false;
                    keyWordMixMatchCharMatchCount = 0;
                    LittlePdfUtil.removeLastList(keyWordLocationList, 1);
                    return;
                }

            }
        }

        private static int leftMatch(String keyWord, String text, int pos) {
            int matchCount = 0, keyWordLen = keyWord.length();
            for (int i = 0, len = text.length(); i < len; i++) {
                int matchStart = pos + matchCount;
                if (matchStart >= keyWordLen) {
                    break;
                }
                if (Objects.equals(keyWord.charAt(matchStart), text.charAt(i))) {
                    matchCount++;
                } else if (matchCount > 0) {
                    //当字符匹配过程中,出现一个不匹配的字符即为不匹配
                    return -1;
                }
            }
            if (matchCount > 0) {
                return matchCount;
            }
            return -1;
        }

        /**
         * 判断是否为mixMatch
         * 返回 判断结果
         */
        private static MaybeMixMatch isMixMatch(String keyWord, String text) {
            int matchCount = 0, firstMatchStart = -1;
            for (int i = 0, len = text.length(); i < len; i++) {
                int matchStart = matchCount;
                if (Objects.equals(keyWord.charAt(matchStart), text.charAt(i))) {
                    if (firstMatchStart == -1) {
                        firstMatchStart = i;
                    }
                    matchCount++;
                } else if (matchCount > 0) {
                    firstMatchStart = -1;
                    matchCount = 0;
                }

            }
            if (matchCount > 0) {
                MaybeMixMatch maybeMixMatch = new MaybeMixMatch(true);
                maybeMixMatch.setMatchStart(firstMatchStart);
                maybeMixMatch.setMatchCount(matchCount);
                return maybeMixMatch;
            }
            return MaybeMixMatch.NOT_MIX_MATCH;
        }



        public static enum KeyWordMatchType {
            MATCH_LAST,
            MATCH_FIRST;
        }

        private KeyWordLocation toKeyWordLocation(String text, Integer keywordMatchType) {
            KeyWordLocation keyWordLocation = new KeyWordLocation();
            keyWordLocation.setKeyWordTextBlockHeight(this.getHeight());
            keyWordLocation.setKeyWordTextBlockWidth(this.getWidth());
            keyWordLocation.setLlx(this.getLlx());
            keyWordLocation.setLly(this.getLly());
            keyWordLocation.setUrx(this.getUrx());
            keyWordLocation.setUry(this.getUry());
            keyWordLocation.setText(text);
            keyWordLocation.setPageNum(pageNum);
            keyWordLocation.setKeywordMatchType(keywordMatchType);
            return keyWordLocation;
        }

        private KeyWordCharLocation toKeyWordCharLocation(String text) {
            KeyWordCharLocation keyWordCharLocation = new KeyWordCharLocation();
            keyWordCharLocation.setKeyWordTextBlockHeight(this.getHeight());
            keyWordCharLocation.setKeyWordTextBlockWidth(this.getWidth());
            keyWordCharLocation.setLlx(this.getLlx());
            keyWordCharLocation.setLly(this.getLly());
            keyWordCharLocation.setUrx(this.getUrx());
            keyWordCharLocation.setUry(this.getUry());
            keyWordCharLocation.setText(text);
            keyWordCharLocation.setPageNum(pageNum);
            keyWordCharLocation.setKeywordMatchType(KEYWORD_MATCH_TYPE_MIX);
            return keyWordCharLocation;
        }

        public static KeyWordCharLocation getMidChar(String keyWord, List<KeyWordCharLocation> mixMatchCharList) {
            int midIndex = keyWord.length() / 2, count = 0;
            for (int i = 0, len = mixMatchCharList.size(); i < len; i++) {
                KeyWordCharLocation keyWordCharLocation = mixMatchCharList.get(i);
                count += keyWordCharLocation.getMixMatchCount();
                if (count >= midIndex) {
                    return keyWordCharLocation;
                }
            }
            return mixMatchCharList.get(0);
        }
    }

    @Data
    private static class MaybeMixMatch {
        private boolean mixMatch;
        /**
         * 开始匹配的位置
         */
        private int matchStart;
        /**
         * 匹配的字符数量
         */
        private int matchCount;

        public MaybeMixMatch(boolean mixMatch) {
            this.mixMatch = mixMatch;
        }


        public static final MaybeMixMatch NOT_MIX_MATCH = new MaybeMixMatch(false);


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


        /**
         * 关键词匹配类型 参见keyFinder
         */
        private Integer keywordMatchType;

        /**
         * 关键词被解析成多个TextRenderInfo
         */
        private List<KeyWordCharLocation> mixMatchCharList;

        /**
         * 匹配的字符数
         */
        private Integer allMatchCount;

    }

    /**
     * 混乱匹配模式下的 打散的关键词字符位置
     */
    @Data
    public static class KeyWordCharLocation extends KeyWordLocation {
        /**
         * 混乱匹配时的起始位置
         */
        private Integer mixMatchStart;

        /**
         * 匹配的字符数
         */
        private Integer mixMatchCount;


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
