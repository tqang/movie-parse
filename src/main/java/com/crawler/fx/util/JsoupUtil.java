package com.crawler.fx.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class JsoupUtil {

    private static Logger logger = LoggerFactory.getLogger(JsoupUtil.class);

    private final static String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    static {
        System.setProperty("https.proxySet", PropertiesUtil.getProperty("https.proxy.enable", "true"));
        System.getProperties().put("https.proxyHost", PropertiesUtil.getProperty("https.proxy.host", "127.0.0.1"));
        System.getProperties().put("https.proxyPort", PropertiesUtil.getProperty("https.proxy.port", "1080"));
    }

    public static Document get(String url) throws IOException {
        try {
            return Jsoup.connect(url).ignoreContentType(true)
                    .userAgent(AGENT)
                    //这个很重要 否则会报HTTP error fetching URL. Status=404
                    .ignoreHttpErrors(true)
                    .timeout(Integer.parseInt(PropertiesUtil.getProperty("jsoup.timeout", "5000"))).get();
        } catch (IOException e) {
            logger.error("get error", e);
            throw e;
        }
    }
}
