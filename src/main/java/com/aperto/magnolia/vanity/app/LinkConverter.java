package com.aperto.magnolia.vanity.app;

import info.magnolia.ui.form.field.converter.BaseIdentifierToPathConverter;

import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

/**
 * Handles input of links by urls (external) or absolute paths (internal).
 *
 * @author philipp.guettler
 * @since 06.03.14
 */
public class LinkConverter extends BaseIdentifierToPathConverter {

    @Override
    public String convertToModel(final String path, final Class<? extends String> targetType, final Locale locale) throws ConversionException {
        String result = path;
        if (!isExternalLink(path) && isNotBlank(path)) {
            result = super.convertToModel(path, targetType, locale);
        }
        return result;
    }

    @Override
    public String convertToPresentation(final String uuid, final Class<? extends String> targetType, final Locale locale) throws ConversionException {
        String result = uuid;
        if (!isExternalLink(uuid) && isNotBlank(uuid)) {
            result = super.convertToPresentation(uuid, targetType, locale);
        }
        return result;
    }

    /**
     * Checks, if the link starts with a web protocol.
     *
     * @param linkValue to check
     * @return true for external link
     */
    public static boolean isExternalLink(final String linkValue) {
        return startsWithIgnoreCase(linkValue, "https://") || startsWithIgnoreCase(linkValue, "http://");
    }
}
