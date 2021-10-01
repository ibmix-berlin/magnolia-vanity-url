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
import info.magnolia.ui.datasource.DatasourceType;
import info.magnolia.ui.datasource.optionlist.Option;
import info.magnolia.ui.datasource.optionlist.OptionListDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

import static com.aperto.magnolia.vanity.VanityUrlService.DEF_SITE;
import static info.magnolia.jcr.util.NodeTypes.ContentNode;
import static info.magnolia.jcr.util.NodeUtil.asList;
import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Extends for site select options.
 *
 * @author frank.sommer
 * @since 05.05.14
 */
@DatasourceType("siteListDatasource")
public class SiteSelectFieldDefinition extends OptionListDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteSelectFieldDefinition.class);
    private static final String SITE_LOCATION = "/modules/multisite/config/sites";

    public SiteSelectFieldDefinition() {
        setName("sitelist");
    }

    @Override
    public List<Option> getOptions() {
        List<Option> options = new ArrayList<>();

        final List<Node> nodes = getNodes();
        if (nodes.isEmpty()) {
            LOGGER.debug("No site nodes found.");
            options.add(createOptionDefinition(DEF_SITE));
        } else {
            LOGGER.debug("{} site nodes found.", nodes.size());
            for (Node node : nodes) {
                options.add(createOptionDefinition(NodeUtil.getName(node)));
            }
        }
        return options;
    }

    private Option createOptionDefinition(final String name) {
        final Option def = new Option();
        def.setName(name);
        def.setLabel(name);
        def.setValue(name);
        return def;
    }

    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();

        try {
            MgnlContext.doInSystemContext(new MgnlContext.RepositoryOp() {
                @Override
                public void doExec() throws RepositoryException {
                    Session jcrSession = MgnlContext.getJCRSession(CONFIG);
                    if (jcrSession.nodeExists(SITE_LOCATION)) {
                        Node siteBaseNode = jcrSession.getNode(SITE_LOCATION);
                        nodes.addAll(asList(NodeUtil.getNodes(siteBaseNode, ContentNode.NAME)));
                    }
                }
            });
        } catch (RepositoryException e) {
            LOGGER.error("Error getting site nodes.", e);
        }

        return nodes;
    }
}
