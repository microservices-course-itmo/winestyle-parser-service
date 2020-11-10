package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
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
public class ScrapingService {
    private final ProxyService proxyService;
    private IWebPageLoader loader;
    private int timeout;

    public ScrapingService() {
        proxyService = new ProxyService();
        loader = new SimpleWebPageLoader();
        timeout = 0;
    }

    /**
     * Достаем док из ссылки
     *
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
            catch (HttpStatusException e) {
                log.error("An error occurs whilst fetching the URL!", e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (timeout > 0) Thread.sleep(timeout);
        return doc;
    }


    public void initProxy(int maxTimeout) {
        loader = proxyService.getLoader(maxTimeout);
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
