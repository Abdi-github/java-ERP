package ch.swiftapp.erp.shared.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Internationalization configuration for Swiss multilingual support.
 * <p>
 * Default locale: de-CH (Swiss German).
 * Supported: de, fr, it, en.
 * Locale can be switched via {@code ?lang=fr} query parameter.
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        var source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        // Allow hot-reload of message files (0 = always check; use -1 in production)
        source.setCacheSeconds(0);
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new CookieLocaleResolver("erp-locale");
        resolver.setDefaultLocale(Locale.of("de", "CH"));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}

