package com.wine.to.up.winestyle.parser.service.configuration;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        class StringToEnumConverter implements Converter<String, AlcoholType> {
            @Override
            public AlcoholType convert(String source) {
                return AlcoholType.valueOf(source.toUpperCase());
            }
        }
        registry.addConverter(new StringToEnumConverter());
    }
}
