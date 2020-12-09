package com.wine.to.up.winestyle.parser.service.configuration;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        class AlcoholTypeStringToEnumConverter implements Converter<String, AlcoholType> {
            @Override
            public AlcoholType convert(String source) {
                return AlcoholType.valueOf(source.toUpperCase());
            }
        }
        class CityStringToEnumConverter implements Converter<String, City> {
            @Override
            public City convert(String source) {
                return City.valueOf(source.toUpperCase());
            }
        }
        registry.addConverter(new AlcoholTypeStringToEnumConverter());
        registry.addConverter(new CityStringToEnumConverter());
    }
}
