package com.aperto.magnolia.vanity.app;

/*
 * #%L
 * magnolia-vanity-url Magnolia Module
 * %%
 * Copyright (C) 2013 - 2014 Aperto AG
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Action to see the big QR code image for a vanity url.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class QrCodeViewAction extends AbstractAction<AbstractItemActionDefinition> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeViewAction.class);

    private final AbstractJcrNodeAdapter _nodeItemToEdit;
    private final LocationController _locationController;
    private VanityUrlService _vanityUrlService;

    @Inject
    public QrCodeViewAction(AbstractItemActionDefinition definition, AbstractJcrNodeAdapter nodeItemToEdit, LocationController locationController) {
        super(definition);
        _nodeItemToEdit = nodeItemToEdit;
        _locationController = locationController;
    }

    @Override
    public void execute() throws ActionExecutionException {
        LOGGER.debug("Execute preview action ...");
        Node node = _nodeItemToEdit.getJcrItem();
        String link = _vanityUrlService.createImageLink(node);
        if (isNotEmpty(link)) {
            Location location = new DefaultLocation(LOCATION_TYPE_APP, getDefinition().getAppName(), getDefinition().getSubAppId(), link);
            _locationController.goTo(location);
        }
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }
}
