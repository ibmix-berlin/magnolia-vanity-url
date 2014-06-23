package com.aperto.magnolia.vanity.app;

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
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static info.magnolia.jcr.util.NodeTypes.ContentNode;
import static info.magnolia.jcr.util.NodeUtil.asList;
import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Extends for site select options.
 *
 * @author frank.sommer
 * @since 05.05.14
 */
public class SiteSelectFieldDefinition extends SelectFieldDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteSelectFieldDefinition.class);
    private static final String SITE_LOCATION = "/modules/multisite/config/sites";

    @Override
    public List<SelectFieldOptionDefinition> getOptions() {
        final List<SelectFieldOptionDefinition> options = new ArrayList<>();

        final List<Node> nodes = getNodes();
        if (nodes.isEmpty()) {
            LOGGER.debug("No site nodes found.");
            options.add(createOptionDefinition("default", true));
        } else {
            LOGGER.debug("{} site nodes found.", nodes.size());
            for (Node node : nodes) {
                options.add(createOptionDefinition(NodeUtil.getName(node), options.isEmpty()));
            }
        }

        return options;
    }

    private SelectFieldOptionDefinition createOptionDefinition(final String name, final boolean selected) {
        final SelectFieldOptionDefinition def = new SelectFieldOptionDefinition();
        def.setName(name);
        def.setLabel(name);
        def.setValue(name);
        def.setSelected(selected);
        return def;
    }

    protected List<Node> getNodes() {
        List<Node> nodes = Collections.emptyList();

        try {
            Session jcrSession = MgnlContext.getJCRSession(CONFIG);
            if (jcrSession.nodeExists(SITE_LOCATION)) {
                Node siteBaseNode = jcrSession.getNode(SITE_LOCATION);
                nodes = asList(NodeUtil.getNodes(siteBaseNode, ContentNode.NAME));
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error getting site nodes.", e);
        }

        return nodes;
    }
}
