package com.aperto.magnolia.vanity;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static info.magnolia.cms.beans.config.ContentRepository.WEBSITE;
import static info.magnolia.cms.core.search.Query.SQL;
import static info.magnolia.cms.util.NodeDataUtil.getString;
import static info.magnolia.freemarker.FreemarkerUtil.createTemplateName;

/**
 * Collects vanity urls and page handles for overview in AdminCentral page.
 *
 * @author oliver.blum, Aperto AG
 * @since 24.01.12
 */
public class VanityUrlPage extends TemplatedMVCHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlPage.class);
    private static final String ND_VANITY = "vanityUrl";
    private static final String QUERY = "SELECT * from mgnl:content where " + ND_VANITY + " IS NOT NULL";

    public VanityUrlPage(String name, HttpServletRequest request, HttpServletResponse response) {
        super(name, request, response);
        setI18nBasename("com.aperto.magnolia.vanity.messages");
    }

    public Map<String, String> getUriListOfVanityUrl() {
        Map<String, String> uriList = new TreeMap<String, String>();
        try {
            Query query = MgnlContext.getQueryManager(WEBSITE).createQuery(QUERY, SQL);
            QueryResult queryResult = query.execute();
            Collection<Content> result = queryResult.getContent();
            if (!result.isEmpty()) {
                for (Content content : result) {
                    uriList.put(content.getHandle(), getString(content, ND_VANITY));
                }
            }
        } catch (RepositoryException e) {
            LOGGER.warn("Can't execute query for vanity urls.", e);
        }
        return uriList;
    }

    public Messages getMessages() {
        return getMsgs();
    }

    @Override
    protected String getTemplateName(String viewName) {
        return createTemplateName(getClass(), "ftl");
    }
}