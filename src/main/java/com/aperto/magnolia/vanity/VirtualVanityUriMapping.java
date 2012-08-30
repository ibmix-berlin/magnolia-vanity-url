package com.aperto.magnolia.vanity;

import info.magnolia.cms.beans.config.QueryAwareVirtualURIMapping;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.context.MgnlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import static info.magnolia.cms.beans.config.ContentRepository.WEBSITE;
import static info.magnolia.cms.core.search.Query.SQL;
import static info.magnolia.link.LinkUtil.createAbsoluteLink;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Virtual Uri Mapping of vanity URLs.
 * Checks if current uri is set as vanity URL and redirect to the page which has set this vanity url.
 *
 * @author diana.racho (Aperto AG)
 */
public class VirtualVanityUriMapping implements QueryAwareVirtualURIMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualVanityUriMapping.class);
    private static final String QUERY = "select * from mgnl:content where vanityUrl = ''{0}''";

    // CHECKSTYLE:OFF
    @Override
    public MappingResult mapURI(String uri) {
        // CHECKSTYLE:ON
        return mapURI(uri, null);
    }

    // CHECKSTYLE:OFF
    @Override
    public MappingResult mapURI(String uri, String queryString) {
        // CHECKSTYLE:ON
        MappingResult result = null;
        try {
            if (isVanityCandidate(uri)) {
                String toUri = getUriOfVanityUrl(uri);
                if (isNotBlank(toUri)) {
                    if (isNotBlank(queryString)) {
                        toUri = toUri.concat(queryString);
                    }
                    result = new MappingResult();
                    result.setToURI(toUri);
                    result.setLevel(uri.length());
                }
            }
        } catch (PatternSyntaxException e) {
            LOGGER.error("A vanity url exclude pattern is not set correctly.", e);
        }
        return result;
    }

    private boolean isVanityCandidate(String uri) {
        boolean contentUri = true;
        Map<String, String> excludes = VanityUrlModule.getInstance().getExcludes();
        if (excludes != null) {
            for (String exclude : excludes.values()) {
                if (isNotEmpty(uri) && isNotEmpty(exclude) && uri.matches(exclude)) {
                    contentUri = false;
                    break;
                }
            }
        }
        return contentUri;
    }

    private String getUriOfVanityUrl(String vanityUrl) {
        String uri = EMPTY;
        String searchQuery = MessageFormat.format(QUERY, new String[]{vanityUrl});
        try {
            Query query = MgnlContext.getQueryManager(WEBSITE).createQuery(searchQuery, SQL);
            QueryResult queryResult = query.execute();
            List<Content> result = (List<Content>) queryResult.getContent();
            if (!result.isEmpty()) {
                String contextPath = MgnlContext.getWebContext().getRequest().getContextPath();
                uri = "redirect:" + removeStart(createAbsoluteLink(result.get(0)), contextPath);
            }
        } catch (RepositoryException e) {
            LOGGER.warn("Can't check correct template.", e);
        }
        return uri;
    }

}
