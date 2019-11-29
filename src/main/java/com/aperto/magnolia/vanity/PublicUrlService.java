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

import info.magnolia.link.LinkUtil;

import javax.jcr.Node;

import static com.aperto.magnolia.vanity.VanityUrlService.getNodeFromId;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Service for creating the public urls from author perspective.
 *
 * @author frank.sommer
 * @since 16.10.14
 */
public interface PublicUrlService {
    /**
     * Builds the vanity url for the public instance. Needed for the qr code generation on author instance.
     *
     * @param node vanity url node
     * @return vanity url
     */
    String createVanityUrl(Node node);

    /**
     * Creates the public url for displaying as target link in app view.
     *
     * @param node vanity url node
     * @return public url
     */
    String createTargetUrl(Node node);

    default String getExternalLinkFromId(final String nodeId) {
        Node nodeFromId = getNodeFromId(nodeId);
        return nodeFromId == null ? EMPTY : LinkUtil.createExternalLink(nodeFromId);
    }
}
