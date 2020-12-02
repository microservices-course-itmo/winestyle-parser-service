package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class MultiProxyLoader implements IWebPageLoader {
    private static Iterator<IUnstableLoader> iterator;
    private final List<IUnstableLoader> loaderList;
    private final IWebPageLoader defaultLoader;

    public MultiProxyLoader(IWebPageLoader defaultLoader, Collection<IUnstableLoader> proxyLoaders) {
        this.defaultLoader = defaultLoader;
        loaderList = new ArrayList<>(proxyLoaders);
    }

    private synchronized IUnstableLoader getNextProxy() {
        if (loaderList.isEmpty()) return null;
        if (iterator == null || !iterator.hasNext()) {
            iterator = loaderList.iterator();
        }

        return iterator.next();
    }

    private synchronized IWebPageLoader getNextWorking() {
        IUnstableLoader loader;
        while (true) {
            loader = getNextProxy();
            if (loader == null) {
                log.info("Unable to find working proxy. Returning default loader.");
                return defaultLoader;
            }
            if (loader.getFailuresCount() > 2) {
                loaderList.remove(loader);
                log.info("Removing loader form list. " + loaderList.size() + " loaders left.");
            }
            else return loader;
        }
    }

    @Override
    public Document getDocument(String url) throws IOException {
        IWebPageLoader pageLoader = getNextWorking();
        return pageLoader.getDocument(url);
    }
}
