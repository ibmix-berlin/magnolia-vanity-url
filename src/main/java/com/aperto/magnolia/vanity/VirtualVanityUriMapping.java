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

import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.site.ExtendedAggregationState;
import info.magnolia.module.site.Site;
import info.magnolia.virtualuri.VirtualUriMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import static com.aperto.magnolia.vanity.VanityUrlService.DEF_SITE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Virtual Uri Mapping of vanity URLs managed in the vanity url app.
 *
 * @author frank.sommer
 */
public class VirtualVanityUriMapping implements VirtualUriMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualVanityUriMapping.class);

    private Provider<VanityUrlModule> _vanityUrlModule;
    private Provider<VanityUrlService> _vanityUrlService;
    private Provider<ModuleRegistry> _moduleRegistry;

    @Inject
    public void setVanityUrlModule(final Provider<VanityUrlModule> vanityUrlModule) {
        _vanityUrlModule = vanityUrlModule;
    }

    @Inject
    public void setVanityUrlService(final Provider<VanityUrlService> vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }

    @Inject
    public void setModuleRegistry(final Provider<ModuleRegistry> moduleRegistry) {
        _moduleRegistry = moduleRegistry;
    }

    @Override
    public Optional<Result> mapUri(final URI uri) {
        Optional<Result> result = Optional.empty();
        try {
            String vanityUrl = uri.getPath();
            if (isVanityCandidate(vanityUrl)) {
                String toUri = getUriOfVanityUrl(vanityUrl);
                if (isNotBlank(toUri)) {
                    result = Optional.of(new Result(toUri, vanityUrl.length(), this));
                }
            }
        } catch (PatternSyntaxException e) {
            LOGGER.error("A vanity url exclude pattern is not set correctly.", e);
        }
        return result;
    }

    protected boolean isVanityCandidate(String uri) {
        boolean contentUri = !isRootRequest(uri);
        if (contentUri) {
            Map<String, String> excludes = _vanityUrlModule.get().getExcludes();
            for (String exclude : excludes.values()) {
                if (isNotEmpty(exclude) && uri.matches(exclude)) {
                    contentUri = false;
                    break;
                }
            }
        }
        return contentUri;
    }

    private boolean isRootRequest(final String uri) {
        return uri.length() <= 1;
    }

    protected String getUriOfVanityUrl(final String vanityUrl) {
        final String siteName = retrieveSite();
        Node node = null;

        try {
            // do it in system context, so the anonymous need no read rights for using vanity urls
            node = MgnlContext.doInSystemContext(
                (MgnlContext.Op<Node, RepositoryException>) () -> _vanityUrlService.get().queryForVanityUrlNode(vanityUrl, siteName)
            );
        } catch (RepositoryException e) {
            LOGGER.warn("Error on querying for vanity url.", e);
        }

        return node == null ? EMPTY : _vanityUrlService.get().createRedirectUrl(node);
    }

    private String retrieveSite() {
        String siteName = DEF_SITE;

        if (_moduleRegistry.get().isModuleRegistered("multisite")) {
            Site site = ((ExtendedAggregationState) MgnlContext.getAggregationState()).getSite();
            siteName = site.getName();
        }

        return siteName;
    }

    @Override
    public boolean isValid() {
        return _vanityUrlService.get() != null;
    }
}
