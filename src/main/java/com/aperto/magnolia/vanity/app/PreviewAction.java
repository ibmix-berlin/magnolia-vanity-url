package com.aperto.magnolia.vanity.app;

import com.aperto.magnolia.vanity.VanityUrlService;
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
import javax.jcr.Node;

import static info.magnolia.ui.api.location.Location.LOCATION_TYPE_APP;

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
    private VanityUrlService _vanityUrlService;

    @Inject
    public PreviewAction(AbstractItemActionDefinition definition, AbstractJcrNodeAdapter nodeItemToEdit, LocationController locationController) {
        super(definition);
        _nodeItemToEdit = nodeItemToEdit;
        _locationController = locationController;
    }

    @Override
    public void execute() throws ActionExecutionException {
        LOGGER.debug("Execute preview action ...");
        Node node = _nodeItemToEdit.getJcrItem();
        if (node != null) {
            String link = _vanityUrlService.createPreviewUrl(node);
            Location location = new DefaultLocation(LOCATION_TYPE_APP, getDefinition().getAppName(), getDefinition().getSubAppId(), link);
            _locationController.goTo(location);
        }
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }
}
