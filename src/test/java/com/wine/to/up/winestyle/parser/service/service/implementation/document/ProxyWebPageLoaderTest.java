package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class ProxyWebPageLoaderTest {
    private static Proxy proxy = Proxy.NO_PROXY;
    private static ProxyWebPageLoader proxyWebPageLoader;

    @BeforeAll
    static void setUp() {
        proxyWebPageLoader = new ProxyWebPageLoader(proxy);
    }

    @Test
    void getFailuresCountWithWrongUrl() {
        try {
            proxyWebPageLoader.getDocument("failed");
        } catch (IOException | IllegalArgumentException ignore) {
        }
        try {
            proxyWebPageLoader.getDocument("failed");
        } catch (IOException | IllegalArgumentException ignore) {
        }
        assertEquals(2, proxyWebPageLoader.getFailuresCount());
    }

    @Test
    void testToString() {
        assertEquals("ProxyWebPageLoader{proxy=DIRECT}", proxyWebPageLoader.toString());
    }
}