package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
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
public class Scraper {
    private int timeout = 0;
    private final WebPageLoader loader;

    public Scraper() {
        loader = ProxyService.getLoader();
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
                log.error("An error occurs whilst fetching the URL! {} {}", e.getStatusCode(), url, e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (timeout > 0) Thread.sleep(timeout);
        return doc;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
