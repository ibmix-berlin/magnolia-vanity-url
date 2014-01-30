package com.aperto.magnolia.vanity;

import info.magnolia.cms.i18n.Messages;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static info.magnolia.cms.util.QueryUtil.search;
import static info.magnolia.freemarker.FreemarkerUtil.createTemplateName;
import static info.magnolia.jcr.util.NodeUtil.asIterable;
import static info.magnolia.jcr.util.NodeUtil.asList;
import static info.magnolia.jcr.util.PropertyUtil.getValuesStringList;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static org.apache.commons.lang.StringUtils.join;

/**
 * Collects vanity urls and page handles for overview in AdminCentral page.
 *
 * @author oliver.blum, Aperto AG
 * @since 24.01.12
 */
public class VanityUrlPage extends TemplatedMVCHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlPage.class);
    public static final String PN_VANITY = "vanityUrl";
    private static final String QUERY = "select * from [mgnl:page] where " + PN_VANITY + " is not null";

    public VanityUrlPage(String name, HttpServletRequest request, HttpServletResponse response) {
        super(name, request, response);
        setI18nBasename("com.aperto.magnolia.vanity.messages");
    }

    public Map<String, String> getUriListOfVanityUrl() {
        Map<String, String> uriList = new TreeMap<String, String>();
        try {
            List<Node> nodes = queryVanityTargetNodes();
            for (Node node : nodes) {
                if (node.hasProperty(PN_VANITY)) {
                    List<String> values = getValuesStringList(node.getProperty(PN_VANITY).getValues());
                    uriList.put(node.getPath(), join(values.toArray(new String[values.size()]), ','));
                }
            }
        } catch (RepositoryException e) {
            LOGGER.warn("Can't execute query for vanity urls.", e);
        }
        return uriList;
    }

    /**
     * Query for vanity url configuration.
     *
     * @return List of nodes.
     * @throws RepositoryException
     */
    public static List<Node> queryVanityTargetNodes() throws RepositoryException {
        return asList(asIterable(search(WEBSITE, QUERY)));
    }

    public Messages getMessages() {
        return getMsgs();
    }

    @Override
    protected String getTemplateName(String viewName) {
        return createTemplateName(getClass(), "ftl");
    }
}