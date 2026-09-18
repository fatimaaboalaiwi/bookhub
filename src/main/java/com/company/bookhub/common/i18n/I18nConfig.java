package com.company.bookhub.common.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class I18nConfig {

    @Bean
    public MessageSource messageSource() {

        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {

        AcceptHeaderLocaleResolver resolver =
                new AcceptHeaderLocaleResolver();

        resolver.setDefaultLocale(Locale.ENGLISH);

        return resolver;
    }
}