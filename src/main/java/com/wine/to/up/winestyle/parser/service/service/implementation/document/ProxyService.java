package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.UnstableLoader;
import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public final class ProxyService {
    private static final HashSet<UnstableLoader> alive = new HashSet<>();
    @Setter(AccessLevel.PRIVATE)
    private static MultiProxyLoader proxyLoader = null;

    @Value("#{'${spring.jsoup.scraping.proxy.different.source}'.split(';')}")
    private List<String> differentProxiesSourceUrls;
    @Value("#{'${spring.jsoup.scraping.proxy.http.source}'.split(';')}")
    private List<String> httpProxiesSourceUrls;
    @Value("#{'${spring.jsoup.scraping.proxy.socks.source}'.split(';')}")
    private List<String> socksProxiesSourceUrls;
    @Value("${spring.jsoup.scraping.winestyle-main-msk-url}")
    private String connectionTestUrl;

    @Value("${spring.jsoup.scraping.proxy.different.check.enabled}")
    private boolean doCheckDifferentProxies;
    @Value("${spring.jsoup.scraping.proxy.http.check.enabled}")
    private boolean doCheckHttpProxies;
    @Value("${spring.jsoup.scraping.proxy.socks.check.enabled}")
    private boolean doCheckSocksProxies;

    private Proxy convertProxy(String proxyAddress, Proxy.Type proxyType) {
        String[] addressParts = proxyAddress.split(":");
        return new java.net.Proxy(proxyType, new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1])));
    }

    private List<String> getAllProxies(String sourceUrl) {
        try {
            URL url = new URL(sourceUrl);
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

    private Optional<UnstableLoader> getProxyIfAlive(Proxy proxy, int maxTimeout) {
        try {
            Jsoup.connect(connectionTestUrl).proxy(proxy).timeout(maxTimeout).get();
        } catch (IOException | NoSuchElementException e) {
            return Optional.empty();
        }
        return Optional.of(ApplicationContextLocator.getApplicationContext().getBean(ProxyWebPageLoader.class, proxy));
    }

    public void initProxies(int maxTimeout) {
        log.info("Getting proxies");

        List<Future<Optional<UnstableLoader>>> httpProxiesToCheckFutures;
        List<Future<Optional<UnstableLoader>>> socksProxiesToCheckFutures;
        List<Future<Optional<UnstableLoader>>> httpProxiesFutures;
        List<Future<Optional<UnstableLoader>>> socksProxiesFutures;

        List<String> differentProxyAddressesToCheck = new ArrayList<>();
        List<Proxy> httpProxies = new ArrayList<>();
        List<Proxy> socksProxies = new ArrayList<>();
        List<Proxy> httpProxiesToCheck = new ArrayList<>();
        List<Proxy> socksProxiesToCheck = new ArrayList<>();

        if (doCheckDifferentProxies) {
            differentProxyAddressesToCheck = differentProxiesSourceUrls.stream().flatMap(url -> getAllProxies(url).stream()).collect(Collectors.toList());
            httpProxiesToCheck = differentProxyAddressesToCheck.stream().map(proxy -> convertProxy(proxy, Proxy.Type.HTTP)).collect(Collectors.toList());
            socksProxiesToCheck = differentProxyAddressesToCheck.stream().map(proxy -> convertProxy(proxy, Proxy.Type.SOCKS)).collect(Collectors.toList());
        }

        if (doCheckHttpProxies) {
            httpProxies = httpProxiesSourceUrls.stream().flatMap(url -> getAllProxies(url).stream()).map(proxy -> convertProxy(proxy, Proxy.Type.HTTP)).collect(Collectors.toList());
        }

        if (doCheckSocksProxies) {
            socksProxies = socksProxiesSourceUrls.stream().flatMap(url -> getAllProxies(url).stream()).map(proxy -> convertProxy(proxy, Proxy.Type.SOCKS)).collect(Collectors.toList());
        }

        ExecutorService proxiesCheckThreadPool = Executors.newFixedThreadPool(differentProxyAddressesToCheck.size() * 2 + httpProxies.size() + socksProxies.size());
        log.info("Loaded list of {} proxies. Checking", differentProxyAddressesToCheck.size() + httpProxies.size() + socksProxies.size());

        httpProxiesToCheckFutures = httpProxiesToCheck.stream().map(proxy -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxy, maxTimeout), proxiesCheckThreadPool)).collect(Collectors.toList());

        socksProxiesToCheckFutures = socksProxiesToCheck.stream().map(proxy -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxy, maxTimeout), proxiesCheckThreadPool)).collect(Collectors.toList());

        httpProxiesFutures = httpProxies.stream().map(proxy -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxy, maxTimeout), proxiesCheckThreadPool)).collect(Collectors.toList());

        socksProxiesFutures = socksProxies.stream().map(proxy -> CompletableFuture.supplyAsync(() -> getProxyIfAlive(proxy, maxTimeout), proxiesCheckThreadPool)).collect(Collectors.toList());

        int httpProxiesAliveCount = 0;
        int socksProxiesAliveCount = 0;

        alive.clear();

        httpProxiesAliveCount += addAlive(httpProxiesToCheckFutures);
        httpProxiesAliveCount += addAlive(httpProxiesFutures);

        socksProxiesAliveCount += addAlive(socksProxiesToCheckFutures);
        socksProxiesAliveCount += addAlive(socksProxiesFutures);

        log.info("Got {} suitable proxies (HTTP : {}, Socks : {})", alive.size(), httpProxiesAliveCount, socksProxiesAliveCount);

        setProxyLoader(new MultiProxyLoader(ApplicationContextLocator.getApplicationContext().getBean(SimpleWebPageLoader.class), alive));
    }

    private int addAlive(List<Future<Optional<UnstableLoader>>> proxyFutures) {
        AtomicInteger aliveCounter = new AtomicInteger();
        for (Future<Optional<UnstableLoader>> future : proxyFutures) {
            try {
                future.get().ifPresent(proxy -> {
                    alive.add(proxy);
                    aliveCounter.getAndIncrement();
                });
            } catch (ExecutionException e) {
                log.debug("An exception occurred while checking proxy asynchronously", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return aliveCounter.get();
    }

    public static WebPageLoader getLoader() {
        if (proxyLoader == null) {
            return ApplicationContextLocator.getApplicationContext().getBean(SimpleWebPageLoader.class);
        }
        return proxyLoader;
    }
}
