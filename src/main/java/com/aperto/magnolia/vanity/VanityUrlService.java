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
import info.magnolia.link.LinkUtil;
import org.apache.jackrabbit.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static com.aperto.magnolia.vanity.app.LinkConverter.isExternalLink;
import static info.magnolia.cms.util.RequestDispatchUtil.REDIRECT_PREFIX;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.jcr.util.SessionUtil.getNodeByIdentifier;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static javax.jcr.query.Query.JCR_SQL2;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Query service for vanity url nodes in vanity url workspace.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VanityUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlService.class);

    private static final String QUERY = "select * from [mgnl:vanityUrl] where vanityUrl = $vanityUrl and site = $site";
    public static final String NN_IMAGE = "qrCode";
    public static final String DEF_SITE = "default";
    public static final String PN_SITE = "site";
    public static final String PN_VANITY_URL = "vanityUrl";
    public static final String PN_LINK = "link";
    public static final String PN_SUFFIX = "linkSuffix";

    @Inject
    @Named(value = "magnolia.contextpath")
    private String _contextPath = "";

    private VanityUrlModule _vanityUrlModule;

    /**
     * Creates the redirect url for uri mapping.
     *
     * @param node vanity url node
     * @return redirect url
     */
    public String createRedirectUrl(final Node node) {
        String redirectUri = createTargetLink(node);
        if (isNotEmpty(redirectUri)) {
            redirectUri = REDIRECT_PREFIX + redirectUri;
        }
        return redirectUri;
    }

    /**
     * Creates the public url for displaying as target link in app view.
     *
     * @param node vanity url node
     * @return public url
     */
    public String createPublicUrl(final Node node) {
        PublicUrlService publicUrlService = _vanityUrlModule.getPublicUrlService();
        return publicUrlService.createTargetUrl(node);
    }

    /**
     * Creates the vanity url for public instance, stored in qr code.
     *
     * @param node vanity url node
     * @return vanity url
     */
    public String createVanityUrl(final Node node) {
        PublicUrlService publicUrlService = _vanityUrlModule.getPublicUrlService();
        return publicUrlService.createVanityUrl(node);
    }

    /**
     * Creates the preview url for app preview.
     *
     * @param node vanity url node
     * @return preview url
     */
    public String createPreviewUrl(final Node node) {
        return createTargetLink(node);
    }

    private String createTargetLink(final Node node) {
        String url = EMPTY;
        if (node != null) {
            url = getString(node, PN_LINK, EMPTY);
            if (isNotEmpty(url)) {
                if (!isExternalLink(url)) {
                    String link = getLinkFromId(url);
                    url = removeStart(defaultString(link), _contextPath);
                }
                url += getString(node, PN_SUFFIX, EMPTY);
            }
        }
        return url;
    }

    /**
     * Create the link to the qr image without context path.
     *
     * @param node vanity url node
     * @return link to qr image
     */
    public String createImageLink(final Node node) {
        String link = EMPTY;
        try {
            if (node != null && node.hasNode(NN_IMAGE)) {
                link = LinkUtil.createLink(node.getNode(NN_IMAGE));
                link = removeStart(defaultString(link), _contextPath);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error creating link to image property.", e);
        }
        return link;
    }

    /**
     * Query for a vanity url node.
     *
     * @param vanityUrl vanity url from request
     * @param siteName  site name from aggegation state
     * @return first vanity url node of result or null, if nothing found
     */
    public Node queryForVanityUrlNode(final String vanityUrl, final String siteName) {
        Node node = null;

        try {
            Session jcrSession = MgnlContext.getJCRSession(VanityUrlModule.WORKSPACE);
            QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(QUERY, JCR_SQL2);
            query.bindValue(PN_VANITY_URL, new StringValue(vanityUrl));
            query.bindValue(PN_SITE, new StringValue(siteName));
            QueryResult queryResult = query.execute();
            NodeIterator nodes = queryResult.getNodes();
            if (nodes.hasNext()) {
                node = nodes.nextNode();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error message.", e);
        }

        return node;
    }

    /**
     * Override for testing.
     */
    protected String getLinkFromId(final String url) {
        return LinkUtil.createLink(getNodeByIdentifier(WEBSITE, url));
    }

    @Inject
    public void setVanityUrlModule(final VanityUrlModule vanityUrlModule) {
        _vanityUrlModule = vanityUrlModule;
    }
}
