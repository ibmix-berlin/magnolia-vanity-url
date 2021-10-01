package com.aperto.magnolia.vanity;

/*
 * #%L
 * magnolia-vanity-url Magnolia Module
 * %%
 * Copyright (C) 2013 - 2014 Aperto AG
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;

import static com.aperto.magnolia.vanity.VanityUrlService.PN_LINK;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_SUFFIX;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static com.aperto.magnolia.vanity.VanityUrlService.isExternalLink;
import static info.magnolia.jcr.util.NodeUtil.getPathIfPossible;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.replaceOnce;

/**
 * Alternative simple implementation for the {@link com.aperto.magnolia.vanity.PublicUrlService}.
 * Uses just the configured server prefix for external link creation.
 *
 * @author frank.sommer
 * @since 16.10.14
 */
public class SimplePublicUrlService implements PublicUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePublicUrlService.class);

    @Inject
    @Named(value = "magnolia.contextpath")
    private String _contextPath = EMPTY;

    private String _targetServerPrefix = "http://www.demo-project.com/context";

    @Override
    public String createVanityUrl(final Node node) {
        LOGGER.debug("Create vanity url for node {}", getPathIfPossible(node));
        return normalizePrefix() + getString(node, PN_VANITY_URL, EMPTY);
    }

    @Override
    public String createTargetUrl(final Node node) {
        LOGGER.debug("Create target url for node {}", getPathIfPossible(node));
        String url = EMPTY;
        if (node != null) {
            url = getString(node, PN_LINK, EMPTY);
            if (isNotEmpty(url)) {
                if (!isExternalLink(url)) {
                    url = normalizePrefix() + removeContextPath(getExternalLinkFromId(url));
                }
                url += getString(node, PN_SUFFIX, EMPTY);
            }
        }
        return url;
    }

    /**
     * For public removing the context path.
     *
     * @param link to check
     * @return link with removed context path
     */
    private String removeContextPath(String link) {
        String changedLink = link;
        if (isNotEmpty(_contextPath)) {
            changedLink = replaceOnce(changedLink, _contextPath, EMPTY);
        }
        return changedLink;
    }

    private String normalizePrefix() {
        return removeEnd(_targetServerPrefix, "/");
    }

    public void setTargetServerPrefix(final String targetServerPrefix) {
        _targetServerPrefix = targetServerPrefix;
    }
}
