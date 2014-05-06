package com.aperto.magnolia.vanity.app;

import info.magnolia.context.MgnlContext;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.location.DefaultLocation;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.api.location.LocationController;
import info.magnolia.ui.contentapp.detail.action.AbstractItemActionDefinition;
import info.magnolia.ui.vaadin.integration.jcr.AbstractJcrNodeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.aperto.magnolia.vanity.app.LinkConverter.isExternalLink;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.link.LinkUtil.createLink;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static info.magnolia.ui.api.location.Location.LOCATION_TYPE_APP;
import static org.apache.commons.lang.StringUtils.removeStart;

/**
 * Preview action for vanity urls. Opens the website page or the external url in the configured app.
 *
 * @author frank.sommer
 * @since 06.05.14
 */
public class PreviewAction extends AbstractAction<AbstractItemActionDefinition> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewAction.class);

    private final AbstractJcrNodeAdapter _nodeItemToEdit;
    private final LocationController _locationController;

    @Inject
    @Named(value = "magnolia.contextpath")
    private String _contextPath = "";

    @Inject
    public PreviewAction(AbstractItemActionDefinition definition, AbstractJcrNodeAdapter nodeItemToEdit, LocationController locationController) {
        super(definition);
        _nodeItemToEdit = nodeItemToEdit;
        _locationController = locationController;
    }

    @Override
    public void execute() throws ActionExecutionException {
        LOGGER.debug("Execute preview action ...");
        try {
            Node node = _nodeItemToEdit.getJcrItem();
            if (node != null) {
                String link = determinePreviewUrl();
                Location location = new DefaultLocation(LOCATION_TYPE_APP, getDefinition().getAppName(), getDefinition().getSubAppId(), link);
                _locationController.goTo(location);
            }
        } catch (RepositoryException e) {
            throw new ActionExecutionException("Could not execute preview action: ", e);
        }
    }

    private String determinePreviewUrl() throws RepositoryException {
        String previewUrl = "";
        Node node = _nodeItemToEdit.getJcrItem();
        if (node != null) {
            String link = getString(node, "link");
            previewUrl = link;
            if (!isExternalLink(link)) {
                Node websiteNode = MgnlContext.getJCRSession(WEBSITE).getNodeByIdentifier(link);
                String url = createLink(websiteNode);
                // remove context path, EmbeddedPageSubApp add this
                previewUrl = removeStart(url, _contextPath);
            }
        }
        return previewUrl;
    }
}
