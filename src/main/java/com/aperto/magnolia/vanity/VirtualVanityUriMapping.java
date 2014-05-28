package com.aperto.magnolia.vanity;

import info.magnolia.cms.beans.config.QueryAwareVirtualURIMapping;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.ExtendedAggregationState;
import info.magnolia.module.templatingkit.sites.Site;
import info.magnolia.templating.functions.TemplatingFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import static com.aperto.magnolia.vanity.app.LinkConverter.isExternalLink;
import static info.magnolia.cms.util.RequestDispatchUtil.REDIRECT_PREFIX;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Virtual Uri Mapping of vanity URLs managed in the vanity url app.
 *
 * @author frank.sommer
 */
public class VirtualVanityUriMapping implements QueryAwareVirtualURIMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualVanityUriMapping.class);

    private TemplatingFunctions _templatingFunctions;
    private VanityUrlModule _vanityUrlModule;
    private VanityQueryService _vanityQueryService;

    @Inject
    @Named(value = "magnolia.contextpath")
    private String _contextPath = "";

    @Inject
    public void setTemplatingFunctions(TemplatingFunctions templatingFunctions) {
        _templatingFunctions = templatingFunctions;
    }

    @Inject
    public void setVanityUrlModule(VanityUrlModule vanityUrlModule) {
        _vanityUrlModule = vanityUrlModule;
    }

    @Inject
    public void setVanityQueryService(final VanityQueryService vanityQueryService) {
        _vanityQueryService = vanityQueryService;
    }

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
                    if (!containsAny(toUri, "?#") && isNotBlank(queryString)) {
                        toUri = toUri.concat("?" + queryString);
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

    protected boolean isVanityCandidate(String uri) {
        boolean contentUri = !isRootRequest(uri);
        if (contentUri) {
            Map<String, String> excludes = _vanityUrlModule.getExcludes();
            for (String exclude : excludes.values()) {
                if (isNotEmpty(exclude) && uri.matches(exclude)) {
                    contentUri = false;
                    break;
                }
            }
        }
        return contentUri;
    }

    private boolean isRootRequest(final String uri) {
        return uri.length() <= 1;
    }

    protected String getUriOfVanityUrl(final String vanityUrl) {
        String redirectUri = EMPTY;
        final String siteName = retrieveSite();

        try {
            // do it in system context, so the anonymous need no read rights for using vanity urls
            redirectUri = MgnlContext.doInSystemContext(
                new MgnlContext.Op<String, RepositoryException>() {
                    @Override
                    public String exec() throws RepositoryException {
                        Node node = _vanityQueryService.queryForVanityUrlNode(vanityUrl, siteName);
                        return determineRedirectUri(node);
                    }
                }
            );
        } catch (RepositoryException e) {
            LOGGER.warn("Error on querying for vanity url.", e);
        }
        return redirectUri;
    }

    private String determineRedirectUri(final Node node) {
        String redirectUri = EMPTY;
        if (node != null) {
            String link = getString(node, "link");
            String url = link;
            if (!isExternalLink(link)) {
                url = removeStart(_templatingFunctions.link(WEBSITE, link), _contextPath);
            }
            redirectUri = REDIRECT_PREFIX + url + getString(node, "linkSuffix", EMPTY);
        }
        return redirectUri;
    }

    private String retrieveSite() {
        String siteName = "default";

        AggregationState aggregationState = MgnlContext.getAggregationState();
        if (aggregationState instanceof ExtendedAggregationState) {
            Site site = ((ExtendedAggregationState) aggregationState).getSite();
            siteName = site.getName();
        }

        return siteName;
    }
}
