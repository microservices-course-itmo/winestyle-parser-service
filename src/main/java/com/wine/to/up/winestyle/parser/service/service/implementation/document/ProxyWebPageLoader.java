package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;

public class ProxyWebPageLoader implements IUnstableLoader
{
    private final Proxy proxy;
    private int failuresCount;

    public ProxyWebPageLoader(Proxy proxy)
    {
        this.proxy = proxy;
        failuresCount = 0;
    }

    @Override
    public Document getDocument(String url) throws IOException
    {
        try
        {
            Document document = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/85.0.4183.121 " +
                            "Safari/537.36")
                    .proxy(proxy)
                    .get();
            failuresCount = 0;
            return document;
        }
        catch (Exception exception)
        {
            failuresCount++;
            throw exception;
        }
    }

    @Override
    public int getFailuresCount()
    {
        return failuresCount;
    }

    @Override
    public String toString()
    {
        return "ProxyWebPageLoader{proxy=" + proxy + '}';
    }
}
