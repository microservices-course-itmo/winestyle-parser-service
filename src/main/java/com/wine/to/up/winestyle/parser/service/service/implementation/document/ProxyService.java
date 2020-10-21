package com.wine.to.up.winestyle.parser.service.service.implementation.document;

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
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProxyService
{
    private List<String> getAllProxies() {
        try
        {
            URL url = new URL("https://api.proxyscrape.com/?request=getproxies&proxytype=socks5&timeout=10000&country=all");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String inputLine;

            List<String> proxies = new ArrayList<>();
            while ((inputLine = in.readLine()) != null)
                proxies.add(inputLine);
            in.close();
            return proxies;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return List.of();
        }
    }

    private static java.net.Proxy convertProxy(String proxyAddress)
    {
        String[] addressParts = proxyAddress.split(":");
        return new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1])));
    }

    private Proxy getProxyIfAlive(String proxyAddress) {
        Proxy proxy = convertProxy(proxyAddress);

        try
        {
            Jsoup
                .connect("https://winestyle.ru/")
                .proxy(proxy)
                .get();

            return proxy;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public List<Proxy> getProxies() {
        log.info("Getting proxies");
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        List<Proxy> alive = new ArrayList<>();

        List<Future<Proxy>> futures;
        List<String> proxyAddresses = getAllProxies();
        futures = proxyAddresses.stream()
                .map(proxyAddress ->
                        CompletableFuture.supplyAsync(() ->
                                getProxyIfAlive(proxyAddress), threadPool))
                .collect(Collectors.toList());

        for (Future<Proxy> future : futures) {
            try
            {
                Proxy proxyResult = future.get();
                if (proxyResult != null)
                {
                    log.info(proxyResult.toString());
                    alive.add(proxyResult);
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }

        return alive;
    }
}
