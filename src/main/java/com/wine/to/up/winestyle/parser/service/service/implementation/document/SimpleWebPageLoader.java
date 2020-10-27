package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SimpleWebPageLoader implements IWebPageLoader
{
    @Override
    public Document getDocument(String url) throws IOException
    {
        return Jsoup
                .connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/85.0.4183.121 " +
                        "Safari/537.36")
                .get();
    }

    @Override
    public String toString()
    {
        return "SimpleWebPageLoader{}";
    }
}
