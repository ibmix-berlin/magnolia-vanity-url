package com.aperto.magkit.vanity;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;

import static info.magnolia.cms.beans.config.ContentRepository.WEBSITE;
import static info.magnolia.cms.core.search.Query.SQL;

/**
 * Collects Vanity Urls and Real Urls for Overview in Admin Central Page
 *
 * @author oliver.blum, Aperto AG
 * @since 24.01.12
 *
 */
public class VanityURIPage extends TemplatedMVCHandler {

    private static final Logger LOG = LoggerFactory.getLogger(VanityURIPage.class);
    private static final String Query = "SELECT * from nt:base where vanityUrl IS NOT NULL";
    private static final String VANITY = "vanityUrl";

    public VanityURIPage(String name, HttpServletRequest request, HttpServletResponse response) {
        super(name, request, response);
    }


    private List<String> getUriListOfVanityUrl() {
        List<String> uriList = null;
        try {
            Query query = MgnlContext.getQueryManager(WEBSITE).createQuery(Query, SQL);
            QueryResult queryResult = query.execute();
            if (queryResult != null && queryResult.getContent().size() > 0) {
                Collection<Content> list = queryResult.getContent();
                for (Content content : list) {
                    uriList.add("Vanity URL: " + content.getHandle() + ", Real URL: " + content.getNodeData(VANITY));
                }

            }
        } catch (RepositoryException e) {
            LOG.warn("Can't check correct template.", e);
        }
        return uriList;
    }


}
