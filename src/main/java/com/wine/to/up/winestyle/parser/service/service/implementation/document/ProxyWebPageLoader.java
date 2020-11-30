package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.UnstableLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.Proxy;

public class ProxyWebPageLoader implements UnstableLoader {
    private final Proxy proxy;
    private int failuresCount;
    @Value("${spring.jsoup.connection.timeout}")
    private int timeout;
    @Value("${spring.jsoup.connection.user-agent}")
    private String userAgent;

    @Override
    public Document getDocument(String url) throws IOException {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(timeout * 1500)
                    .proxy(proxy)
                    .get();
            failuresCount = 0;
            return document;
        } catch (Exception exception) {
            failuresCount++;
            throw exception;
        }
    }

    @Override
    public int getFailuresCount() {
        return failuresCount;
    }

    @Override
    public String toString() {
        return "ProxyWebPageLoader{proxy=" + proxy + '}';
    }
}
