package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.UnstableLoader;
import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class MultiProxyLoaderTest {
    private MultiProxyLoader multiProxyLoader;
    private WebPageLoader defaultLoader = mock(WebPageLoader.class);
    ArrayList proxyLoaders;
    Iterator<UnstableLoader> iterator = spy(Iterator.class);
    UnstableLoader loader1;
    UnstableLoader loader2;
    UnstableLoader loader3;

    @BeforeEach
    void setUp() {
        Proxy proxy = Proxy.NO_PROXY;
        loader1 = new ProxyWebPageLoader(proxy);
        loader2 = new ProxyWebPageLoader(proxy);
        loader3 = new ProxyWebPageLoader(proxy);
    }


    @Test
    void getDocumentWithLastFailuresMoreThenTwoInLoaderList() {
        ReflectionTestUtils.setField(loader1, "failuresCount", 3);
        ReflectionTestUtils.setField(loader2, "failuresCount", 1);
        ReflectionTestUtils.setField(loader3, "failuresCount", 4);
        proxyLoaders = new ArrayList<UnstableLoader>();
        proxyLoaders.add(loader1);
        proxyLoaders.add(loader2);
        proxyLoaders.add(loader3);
        multiProxyLoader = new MultiProxyLoader(defaultLoader, proxyLoaders);
        ReflectionTestUtils.setField(multiProxyLoader, "iterator", iterator);
        try {
            multiProxyLoader.getDocument("test");
        } catch (IOException e) {
            fail("Test failed! smth wrong", e);
        } catch (IllegalArgumentException ex) {
            assertEquals("Malformed URL: test", ex.getMessage());
        }
        Mockito.verify(iterator, Mockito.times(1)).hasNext();
        ConcurrentLinkedQueue<UnstableLoader> resultList = (ConcurrentLinkedQueue) ReflectionTestUtils
                .getField(multiProxyLoader, "loaderList");
        assertEquals(1, resultList.size());
    }
    @Test
    void getDocumentWithLastFailuresLessThanThreeInLoaderList() {
        ReflectionTestUtils.setField(loader1, "failuresCount", 3);
        ReflectionTestUtils.setField(loader2, "failuresCount", 4);
        ReflectionTestUtils.setField(loader3, "failuresCount", 1);
        proxyLoaders = new ArrayList<UnstableLoader>();
        proxyLoaders.add(loader1);
        proxyLoaders.add(loader2);
        proxyLoaders.add(loader3);
        multiProxyLoader = new MultiProxyLoader(defaultLoader, proxyLoaders);
        ReflectionTestUtils.setField(multiProxyLoader, "iterator", iterator);
        try {
            multiProxyLoader.getDocument("test");
        } catch (IOException e) {
            fail("Test failed! smth wrong", e);
        } catch (IllegalArgumentException ex) {
            assertEquals("Malformed URL: test", ex.getMessage());
        }
        Mockito.verify(iterator, Mockito.times(1)).hasNext();
        ConcurrentLinkedQueue<UnstableLoader> resultList = (ConcurrentLinkedQueue) ReflectionTestUtils
                .getField(multiProxyLoader, "loaderList");
        assertEquals(1, resultList.size());
    }
}