package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Класс, который парсит страницы сайта. Скачивает html и возвращает Document.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScrapingService {

    private static final ProxyService proxyService;
    private static final IWebPageLoader loader;

    static {
         proxyService = new ProxyService();
         loader = proxyService.getLoader();
    }

    public synchronized Proxy getNextProxyRoundRobin() {
        if (it == null || !it.hasNext()) {
            it = proxies.iterator();
        }
        return it.next();
    }

    /**
     * Достаем док из ссылки
     * @param url Ссылка на страницу
     * @return документ 
     * @throws InterruptedException при блокировке потока исполнения Thread.sleep()
     */
    public Document getJsoupDocument(String url) throws InterruptedException {
        Document doc = null;
        while (doc == null) {
            try {
                doc = loader.getDocument(url);
            } catch (SocketException | SocketTimeoutException | SSLException ex) {
                log.error("Couldn't get a connection to website!", ex);
            } // Берем страничку html
            catch (IOException e) {
                log.error("An error occurs whilst fetching the URL!", e);
            }
        }
        return doc;
    }
}
