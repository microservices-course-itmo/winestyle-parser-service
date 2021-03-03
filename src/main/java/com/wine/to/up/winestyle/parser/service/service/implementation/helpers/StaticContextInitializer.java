package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {
    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        ApplicationContextLocator.setApplicationContext(context);
    }
}
