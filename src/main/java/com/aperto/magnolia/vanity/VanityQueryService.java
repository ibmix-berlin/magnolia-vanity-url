package com.aperto.magnolia.vanity;

import info.magnolia.context.MgnlContext;
import org.apache.jackrabbit.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static javax.jcr.query.Query.JCR_SQL2;

/**
 * Query service for vanity url nodes in vanity url workspace.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VanityQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityQueryService.class);
    private static final String QUERY = "select * from [mgnl:vanityUrl] where vanityUrl = $vanityUrl and site = $site";

    /**
     * Query for a vanity url node.
     *
     * @param vanityUrl vanity url from request
     * @param siteName site name from aggegation state
     * @return first vanity url node of result or null, if nothing found
     */
    protected Node queryForVanityUrlNode(final String vanityUrl, final String siteName) {
        Node node = null;

        try {
            Session jcrSession = MgnlContext.getJCRSession("vanityUrls");
            QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(QUERY, JCR_SQL2);
            query.bindValue("vanityUrl", new StringValue(vanityUrl));
            query.bindValue("site", new StringValue(siteName));
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
}
