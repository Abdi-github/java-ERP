package ch.swiftapp.erp.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Shared service that resolves the best-fitting translation value from a
 * collection of translation objects based on the current request locale.
 *
 * <p><b>Resolution order:</b></p>
 * <ol>
 *   <li>Exact language match for the current request locale (e.g. {@code fr})</li>
 *   <li>Fallback to {@code de} — the company's primary language</li>
 *   <li>Any non-blank translation in any supported locale</li>
 *   <li>The original {@code defaultValue} stored on the parent entity</li>
 * </ol>
 *
 * <p>This bean is part of the {@code shared} open module and may be injected
 * in any ERP module service that needs locale-aware field resolution.</p>
 */
@Component
@Slf4j
public class TranslationResolver {

    /** Company primary / fallback locale — Swiss German. */
    public static final String FALLBACK_LOCALE = "de";

    /** All supported BCP-47 language subtags. */
    public static final List<String> SUPPORTED_LOCALES = List.of("de", "fr", "it", "en");

    /**
     * Resolve the best translation value from a collection of translation objects.
     *
     * @param <T>           the translation entity type
     * @param translations  all available translations for the parent entity
     * @param localeGetter  function to extract the locale string from a translation
     * @param valueGetter   function to extract the translated string value
     * @param defaultValue  the original field value on the parent entity (ultimate fallback)
     * @return the best available translated value, never {@code null} if defaultValue is not null
     */
    public <T> String resolve(
            Collection<T> translations,
            Function<T, String> localeGetter,
            Function<T, String> valueGetter,
            String defaultValue
    ) {
        if (translations == null || translations.isEmpty()) {
            return defaultValue;
        }

        String lang = LocaleContextHolder.getLocale().getLanguage();

        Map<String, T> byLocale = translations.stream()
                .filter(t -> localeGetter.apply(t) != null)
                .collect(Collectors.toMap(localeGetter, t -> t, (a, b) -> a));

        // 1. Exact language match
        T found = byLocale.get(lang);
        if (found != null) {
            String value = valueGetter.apply(found);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        // 2. Fallback to "de"
        if (!lang.equals(FALLBACK_LOCALE)) {
            found = byLocale.get(FALLBACK_LOCALE);
            if (found != null) {
                String value = valueGetter.apply(found);
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }

        // 3. Any available translation
        Optional<String> any = translations.stream()
                .map(valueGetter)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
        if (any.isPresent()) {
            return any.get();
        }

        // 4. Default (original entity field)
        return defaultValue;
    }

    /**
     * Build a {@code Map<locale, value>} of all available translations for a field.
     * Used to populate edit forms where operators enter translations for each locale.
     *
     * @param <T>          the translation entity type
     * @param translations all available translations
     * @param localeGetter function to extract the locale string
     * @param valueGetter  function to extract the translated value
     * @return map of locale → translated value (only non-null values are included)
     */
    public <T> Map<String, String> toMap(
            Collection<T> translations,
            Function<T, String> localeGetter,
            Function<T, String> valueGetter
    ) {
        if (translations == null || translations.isEmpty()) {
            return Map.of();
        }
        return translations.stream()
                .filter(t -> localeGetter.apply(t) != null && valueGetter.apply(t) != null)
                .collect(Collectors.toMap(localeGetter, valueGetter, (a, b) -> a));
    }

    /** Returns the list of supported locale codes for use in controllers and views. */
    public static List<String> supportedLocales() {
        return SUPPORTED_LOCALES;
    }
}

