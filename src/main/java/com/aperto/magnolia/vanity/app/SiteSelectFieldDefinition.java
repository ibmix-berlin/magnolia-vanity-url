package com.aperto.magnolia.vanity.app;

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
