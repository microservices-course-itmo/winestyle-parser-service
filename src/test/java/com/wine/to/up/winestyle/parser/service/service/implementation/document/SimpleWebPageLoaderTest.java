package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleWebPageLoaderTest {
    WebPageLoader pageLoader;

    @Test
    void testToString() {
        pageLoader = new SimpleWebPageLoader();
        assertEquals("SimpleWebPageLoader{}", pageLoader.toString());
    }
}