package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.UnstableLoader;
import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiProxyLoader implements WebPageLoader {
    private static Iterator<UnstableLoader> iterator;
    private final ConcurrentLinkedQueue<UnstableLoader> loaderList;
    private final WebPageLoader defaultLoader;

    public MultiProxyLoader(WebPageLoader defaultLoader, Collection<UnstableLoader> proxyLoaders) {
        this.defaultLoader = defaultLoader;
        loaderList = new ConcurrentLinkedQueue<>(proxyLoaders);
    }

    private synchronized UnstableLoader getNextProxy() {
        if (loaderList.isEmpty()) return null;
        if (iterator == null || !iterator.hasNext()) {
            iterator = loaderList.iterator();
        }

        return iterator.next();
    }

    private synchronized WebPageLoader getNextWorking() {
        UnstableLoader loader;
        while (true) {
            loader = getNextProxy();
            if (loader == null) return defaultLoader;
            if (loader.getFailuresCount() > 2) loaderList.remove(loader);
            else return loader;
        }
    }

    @Override
    public Document getDocument(String url) throws IOException {
        WebPageLoader pageLoader = getNextWorking();
        return pageLoader.getDocument(url);
    }
}
