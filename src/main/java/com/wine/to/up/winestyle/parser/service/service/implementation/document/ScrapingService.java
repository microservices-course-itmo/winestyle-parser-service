package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;

/**
 * Класс, который парсит страницы сайта. Скачивает html и возвращает Document.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapingService {

    private static final ProxyService proxyService = new ProxyService();

    private static final List<Proxy> proxies;
    private static Iterator<Proxy> it;

    static {
         proxies = proxyService.getProxies();
    }

    public synchronized Proxy getNextProxyRoundRobin() {
        if (it == null || !it.hasNext()) {
            it = proxies.iterator();
        }
        return it.next();
    }

    public Document getJsoupDocument(String url) throws InterruptedException {
        Document doc = null;
        while (doc == null) {
            try {
                doc = Jsoup
                        .connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/85.0.4183.121 " +
                                "Safari/537.36")
                        .proxy(getNextProxyRoundRobin())
                        .get();
            } catch (SocketTimeoutException | SSLException | ConnectException ex) {
                log.error("Couldn't get a connection to website!", ex);
            } // Берем страничку html
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }
}
