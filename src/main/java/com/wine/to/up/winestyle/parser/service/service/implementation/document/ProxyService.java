package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.UnstableLoader;
import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public final class ProxyService {
    private static final List<UnstableLoader> alive = new ArrayList<>();
    private static WebPageLoader loader = ApplicationContextLocator.getApplicationContext().getBean(SimpleWebPageLoader.class);

    private ProxyService() {

    }

    private static Proxy convertProxy(String proxyAddress) {
        String[] addressParts = proxyAddress.split(":");
        return new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1])));
    }

    private static List<String> getAllProxies() {
        try {
            URL url = new URL("https://api.proxyscrape.com/?request=getproxies&proxytype=socks5&country=all");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            List<String> proxies = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) proxies.add(inputLine);
            in.close();
            return proxies;
        } catch (IOException e) {
            log.error("Cannot get proxies from external list", e);
            return Collections.emptyList();
        }
    }

    private static UnstableLoader getProxyIfAlive(String proxyAddress, int maxTimeout) {
        Proxy proxy = convertProxy(proxyAddress);

        try {
            Jsoup.connect("https://winestyle.ru/").proxy(proxy).timeout(maxTimeout).get();

            log.trace("{} OK", proxyAddress);
            return new ProxyWebPageLoader(proxy);
        } catch (Exception e) {
            return null;
        }
    }

    public static void initProxies(int maxTimeout) {
        log.info("Getting proxies");

        List<Future<UnstableLoader>> futures;
        List<String> proxyAddresses = getAllProxies();
        ExecutorService threadPool = Executors.newFixedThreadPool(proxyAddresses.size());
        log.info("Loaded list of {} proxies. Checking", proxyAddresses.size());
        futures = proxyAddresses.stream().map(proxyAddress -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxyAddress, maxTimeout), threadPool)).collect(Collectors.toList());

        alive.clear();
        for (Future<UnstableLoader> future : futures) {
            try {
                UnstableLoader proxyResult = future.get();
                if (proxyResult != null) {
                    alive.add(proxyResult);
                }
            } catch (ExecutionException e) {
                log.error("An exception occurred while checking proxy asynchronously", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Got {} suitable proxies", alive.size());
    }

    public static WebPageLoader getLoader() {
        if (!alive.isEmpty() && !(loader instanceof MultiProxyLoader)) {
            loader = new MultiProxyLoader(new SimpleWebPageLoader(), alive);
        }
        return loader;
    }
}
