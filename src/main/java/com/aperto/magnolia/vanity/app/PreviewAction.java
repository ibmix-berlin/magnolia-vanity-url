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
import info.magnolia.ui.ValueContext;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.location.DefaultLocation;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.api.location.LocationController;
import info.magnolia.ui.contentapp.action.OpenLocationActionDefinition;
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
public class PreviewAction extends AbstractAction<OpenLocationActionDefinition> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewAction.class);

    private final LocationController _locationController;
    private final ValueContext<Node> _valueContext;
    private VanityUrlService _vanityUrlService;

    @Inject
    public PreviewAction(OpenLocationActionDefinition definition, ValueContext<Node> valueContext, LocationController locationController) {
        super(definition);
        _locationController = locationController;
        _valueContext = valueContext;
    }

    @Override
    public void execute() {
        LOGGER.debug("Execute preview action ...");
        if (_valueContext.getSingle().isPresent()) {
            String link = _vanityUrlService.createPreviewUrl(_valueContext.getSingle().get());
            Location location = new DefaultLocation(LOCATION_TYPE_APP, getDefinition().getAppName(), getDefinition().getSubAppId(), link);
            _locationController.goTo(location);
        }
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }
}
