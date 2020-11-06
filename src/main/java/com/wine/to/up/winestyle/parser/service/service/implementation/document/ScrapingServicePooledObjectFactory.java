package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ScrapingServicePooledObjectFactory extends BasePooledObjectFactory<ScrapingService> {

    @Override
    public ScrapingService create() {
        return new ScrapingService();
    }

    @Override
    public PooledObject<ScrapingService> wrap(ScrapingService scrapingService) {
        return new DefaultPooledObject<>(scrapingService);
    }
}
