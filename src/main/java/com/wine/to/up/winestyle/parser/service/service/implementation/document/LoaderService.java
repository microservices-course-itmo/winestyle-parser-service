package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import com.wine.to.up.winestyle.parser.service.service.WebPageLoader;

public class LoaderService {
    public static WebPageLoader getLoader() {
        return new SimpleWebPageLoader();
    }
}
