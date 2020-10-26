package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Класс, который парсит страницы сайта. Скачивает html и возвращает Document.
 */
@Service
@Slf4j
public class ScrapingService {
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
                doc = Jsoup
                        .connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/85.0.4183.121 " +
                                "Safari/537.36")
                        .get();
            } catch (SocketTimeoutException | SSLException | ConnectException e) {
                log.error("Couldn't get a connection to website!", e);
            } // Берем страничку html
            catch (IOException e) {
                log.error("An error occurs whilst fetching the URL!", e);
            }
        }
        Thread.sleep(625);
        return doc;
    }
}
