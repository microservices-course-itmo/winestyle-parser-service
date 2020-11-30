package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SimpleWebPageLoader implements WebPageLoader {
    @Value("${spring.jsoup.connection.user-agent}")
    private String userAgent;

    @Override
    public Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(userAgent)
                .get();
    }

    @Override
    public String toString() {
        return "SimpleWebPageLoader{}";
    }
}
