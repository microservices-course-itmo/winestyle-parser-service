package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextLocator {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ApplicationContextLocator() {
        super();
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextLocator.applicationContext = applicationContext;
    }

}
