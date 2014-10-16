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


import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.link.LinkUtil;
import info.magnolia.module.templatingkit.sites.Domain;
import info.magnolia.module.templatingkit.sites.Site;
import info.magnolia.module.templatingkit.sites.SiteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import java.util.Collection;

import static com.aperto.magnolia.vanity.VanityUrlService.*;
import static com.aperto.magnolia.vanity.app.LinkConverter.isExternalLink;
import static info.magnolia.jcr.util.NodeUtil.getPathIfPossible;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.jcr.util.SessionUtil.getNodeByIdentifier;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Default implementation for the {@link PublicUrlService}.
 *
 * @author frank.sommer
 * @since 16.10.14
 */
public class DefaultPublicUrlService implements PublicUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPublicUrlService.class);

    private String _targetContextPath = EMPTY;

    @Inject
    @Named(value = "magnolia.contextpath")
    private String _contextPath = EMPTY;

    private SiteManager _siteManager;
    private ServerConfiguration _serverConfiguration;

    @Override
    public String createVanityUrl(final Node node) {
        LOGGER.debug("Create vanity url for node {}", getPathIfPossible(node));
        // default base url is the default
        String baseUrl = _serverConfiguration.getDefaultBaseUrl();
        baseUrl = replaceContextPath(baseUrl);


        // check the site configuration and take the first domain
        String siteName = getString(node, PN_SITE, DEF_SITE);
        if (!DEF_SITE.equals(siteName)) {
            Site site = _siteManager.getSite(siteName);
            Collection<Domain> domains = site.getDomains();
            if (!domains.isEmpty()) {
                Domain firstDomain = domains.iterator().next();
                baseUrl = firstDomain.toString();
            }
        }

        return removeEnd(baseUrl, "/") + getString(node, PN_VANITY_URL, EMPTY);
    }

    /**
     * For public replacing the context path.
     *
     * @param link for replacing
     * @return link with replaced context path
     */
    private String replaceContextPath(String link) {
        String changedLink = link;
        if (isNotEmpty(_contextPath)) {
            changedLink = replaceOnce(changedLink, _contextPath, _targetContextPath);
        }
        return changedLink;
    }

    @Override
    public String createTargetUrl(final Node node) {
        LOGGER.debug("Create target url for node {}", getPathIfPossible(node));
        String url = EMPTY;
        if (node != null) {
            url = getString(node, PN_LINK, EMPTY);
            if (isNotEmpty(url)) {
                if (!isExternalLink(url)) {
                    url = getExternalLinkFromId(url);
                    url = replaceContextPath(url);
                }
                url += getString(node, PN_SUFFIX, EMPTY);
            }
        }
        return url;
    }

    /**
     * Override for testing.
     */
    protected String getExternalLinkFromId(final String url) {
        return LinkUtil.createExternalLink(getNodeByIdentifier(WEBSITE, url));
    }

    @Inject
    public void setSiteManager(final SiteManager siteManager) {
        _siteManager = siteManager;
    }

    @Inject
    public void setServerConfiguration(final ServerConfiguration serverConfiguration) {
        _serverConfiguration = serverConfiguration;
    }

    public void setContextPath(final String contextPath) {
        _contextPath = contextPath;
    }

    public void setTargetContextPath(final String targetContextPath) {
        _targetContextPath = targetContextPath;
    }
}
