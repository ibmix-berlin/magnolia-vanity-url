package com.aperto.magkit.vanity;

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.context.MgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.text.MessageFormat;
import java.util.Collection;

import static info.magnolia.cms.beans.config.ContentRepository.WEBSITE;
import static info.magnolia.cms.core.search.Query.SQL;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithAny;

/**
 * Virtual Uri Mapping of vanity URLs.
 * Checks if current uri is set as vanity URL and redirect to the page which has set this vanity url.
 *
 * @author diana.racho (Aperto AG)
 */
public class VirtualVanityUriMapping implements VirtualURIMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualVanityUriMapping.class);
    private static final String QUERY = "select * from nt:base where vanityUrl = ''{0}''";
    private static final String[] DO_NOT_CHECK_STARTS = {"/docroot", "/.", "/dms/", "/rss/", "/tmp/", "/mediaObject/"};

    // CHECKSTYLE:OFF
    public MappingResult mapURI(String uri) {
        // CHECKSTYLE:ON
        MappingResult result = null;
        if (isContentUri(uri)) {
            String toUri = getUriOfVanityUrl(uri);
            if (isNotBlank(toUri)) {
                result = new MappingResult();
                result.setToURI(toUri);
                result.setLevel(uri.length());
            }
        }
        return result;
    }

    private boolean isContentUri(String uri) {
        return !startsWithAny(uri, DO_NOT_CHECK_STARTS) && !contains(uri, "/resources/");
    }

    private String getUriOfVanityUrl(String vanityUrl) {
        String uri = EMPTY;
        String searchQuery = MessageFormat.format(QUERY, new String[]{vanityUrl});
        try {
            Query query = MgnlContext.getQueryManager(WEBSITE).createQuery(searchQuery, SQL);
            QueryResult queryResult = query.execute();
            if (queryResult != null && queryResult.getContent().size() > 0) {
                Collection<Content> list = queryResult.getContent();
                if (list != null && !list.isEmpty()) {
                    uri = "redirect:" + ((Content) list.toArray()[0]).getHandle() + ".html";
                }
            }
        } catch (RepositoryException e) {
            LOGGER.warn("Can't check correct template.", e);
        }
        return uri;
    }
}