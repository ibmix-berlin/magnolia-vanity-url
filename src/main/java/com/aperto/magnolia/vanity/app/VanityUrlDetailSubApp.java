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
import info.magnolia.cms.core.version.VersionManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.contentapp.ContentSubAppView;
import info.magnolia.ui.contentapp.detail.DetailEditorPresenter;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.contentapp.detail.DetailSubApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Extended detail sub-app - just sets the proper caption.
 *
 * @author frank.sommer
 * @since 13.08.14
 */
public class VanityUrlDetailSubApp extends DetailSubApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlDetailSubApp.class);

    private final VersionManager _versionManager;
    private final SimpleTranslator _i18n;

    @Inject
    protected VanityUrlDetailSubApp(final SubAppContext subAppContext, final ContentSubAppView view, @Named(AdmincentralEventBus.NAME) EventBus adminCentralEventBus, DetailEditorPresenter workbench, VersionManager versionManager, SimpleTranslator i18n) {
        super(subAppContext, view, adminCentralEventBus, workbench, i18n);
        _versionManager = versionManager;
        _i18n = i18n;
    }

    @Override
    protected String getBaseCaption(DetailLocation location) {
        String baseCaption = super.getBaseCaption(location);
        String nodePath = location.getNodePath();
        try {
            Session jcrSession = MgnlContext.getJCRSession(getWorkspace());
            if (jcrSession.nodeExists(nodePath)) {
                Node node = jcrSession.getNode(nodePath);
                // get specific node version if needed
                if (isNotBlank(location.getVersion())) {
                    node = _versionManager.getVersion(node, location.getVersion());
                }
                String vanityUrl = getString(node, PN_VANITY_URL);
                if (isNotBlank(vanityUrl)) {
                    baseCaption = vanityUrl;
                }
            } else {
                baseCaption = _i18n.translate("vanityUrl.detail.caption.newVanityUrl");
            }
        } catch (RepositoryException e) {
            LOGGER.warn("Could not set sub app tab caption for item : {}", nodePath, e);
        }
        return baseCaption;
    }
}
