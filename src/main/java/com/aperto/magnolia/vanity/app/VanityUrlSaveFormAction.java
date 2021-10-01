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
import com.machinezoo.noexception.Exceptions;
import com.vaadin.ui.Notification;
import info.magnolia.cms.core.FileSystemHelper;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.NodeTypes.Resource;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.AlertBuilder;
import info.magnolia.ui.CloseHandler;
import info.magnolia.ui.ValueContext;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.location.LocationController;
import info.magnolia.ui.contentapp.ContentBrowserSubApp;
import info.magnolia.ui.contentapp.Datasource;
import info.magnolia.ui.contentapp.action.CommitAction;
import info.magnolia.ui.contentapp.action.CommitActionDefinition;
import info.magnolia.ui.datasource.ItemResolver;
import info.magnolia.ui.editor.EditorView;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.observation.DatasourceObservation;
import net.glxn.qrgen.QRCode;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static com.aperto.magnolia.vanity.VanityUrlService.NN_IMAGE;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_SITE;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static info.magnolia.cms.beans.runtime.File.PROPERTY_CONTENTTYPE;
import static info.magnolia.cms.beans.runtime.File.PROPERTY_FILENAME;
import static info.magnolia.cms.beans.runtime.File.PROPERTY_LASTMODIFIED;
import static info.magnolia.jcr.util.NodeUtil.getNodeIdentifierIfPossible;
import static info.magnolia.jcr.util.PropertyUtil.getPropertyOrNull;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.jcr.util.PropertyUtil.setProperty;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.stripStart;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.jackrabbit.JcrConstants.JCR_DATA;

/**
 * Saves additional to the form fields a qr code image as preview image.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VanityUrlSaveFormAction extends CommitAction<Node> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlSaveFormAction.class);
    private static final int QR_WIDTH = 500;
    private static final int QR_HEIGHT = 500;
    private static final String MIME_TYPE = "image/png";
    public static final String IMAGE_EXTENSION = ".png";

    private final AppContext _appContext;
    private final LocationController _locationController;
    private final ItemResolver<Node> _itemResolver;
    private SimpleTranslator _simpleTranslator;
    private VanityUrlService _vanityUrlService;
    private NodeNameHelper _nodeNameHelper;
    private FileSystemHelper _fileSystemHelper;

    //CHECKSTYLE:OFF
    @Inject
    public VanityUrlSaveFormAction(CommitActionDefinition definition, CloseHandler closeHandler, ValueContext<Node> valueContext, EditorView<Node> form, Datasource<Node> datasource, DatasourceObservation.Manual datasourceObservation, LocationController locationController, AppContext appContext, ItemResolver<Node> itemResolver) {
        super(definition, closeHandler, valueContext, form, datasource, datasourceObservation);
        _appContext = appContext;
        _locationController = locationController;
        _itemResolver = itemResolver;
    }
    //CHECKSTYLE:ON

    @Override
    protected boolean validateForm() {
        boolean isValid = super.validateForm();
        if (isValid && getValueContext().getSingle().isPresent()) {
            Node node = getValueContext().getSingle().get();
            getForm().write(node);

            String site = getString(node, PN_SITE);
            String vanityUrl = getString(node, PN_VANITY_URL);
            if (site != null && vanityUrl != null) {
                List<Node> nodes = _vanityUrlService.queryForVanityUrlNodes(vanityUrl, site);
                String currentIdentifier = getNodeIdentifierIfPossible(node);
                for (Node resultNode : nodes) {
                    if (!currentIdentifier.equals(getNodeIdentifierIfPossible(resultNode))) {
                        isValid = false;
                        AlertBuilder.alert(_simpleTranslator.translate("actions.commit.failureMessage"))
                            .withLevel(Notification.Type.WARNING_MESSAGE)
                            .withBody(_simpleTranslator.translate("vanityUrl.errorMessage.notUnique"))
                            .withOkButtonCaption(_simpleTranslator.translate("button.ok"))
                            .buildAndOpen();
                        break;
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    protected void write() {
        getValueContext().getSingle().ifPresent(Exceptions.wrap().consumer(
            item -> {
                setNodeName(item);
                setPreviewImage(item);

                getDatasource().commit(item);
                getDatasourceObservation().trigger();
            }
        ));

        // update location after saving content
        _locationController.goTo(
            new ContentBrowserSubApp.BrowserLocation(
                _appContext.getName(), "browser", getValueContext().getSingle().map(_itemResolver::getId).orElse("")
            )
        );
    }

    private String getNormalizedVanityUrl(final Node node) {
        String vanityUrl = getString(node, PN_VANITY_URL);
        vanityUrl = stripStart(trimToEmpty(vanityUrl), "/");
        return vanityUrl;
    }

    private void setPreviewImage(final Node node) {
        String url = _vanityUrlService.createVanityUrl(node);
        String fileName = trim(strip(getString(node, PN_VANITY_URL, ""), "/")).replace("/", "-");
        File tmpQrCodeFile = _fileSystemHelper.getTempDirectory();
        UploadReceiver uploadReceiver = new UploadReceiver(tmpQrCodeFile, _simpleTranslator);

        try (FileOutputStream outputStream = (FileOutputStream) uploadReceiver.receiveUpload(fileName + IMAGE_EXTENSION, MIME_TYPE)) {
            QRCode.from(url).withSize(QR_WIDTH, QR_HEIGHT).writeTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Error writing temp file for qr code.", e);
        }

        try (FileInputStream qrCodeInputStream = new FileInputStream(uploadReceiver.getFile())) {
            Node qrNode;
            if (node.hasNode(NN_IMAGE)) {
                qrNode = node.getNode(NN_IMAGE);
            } else {
                qrNode = node.addNode(NN_IMAGE, Resource.NAME);
            }

            populateItem(qrCodeInputStream, qrNode, fileName);
        } catch (RepositoryException e) {
            LOGGER.error("Error on saving preview image for vanity url.", e);
        } catch (IOException e) {
            LOGGER.error("Error reading qr image temp file.", e);
        }
    }

    private void populateItem(InputStream inputStream, Node qrCodeNode, final String fileName) {
        if (inputStream != null) {
            try {
                Property data = getPropertyOrNull(qrCodeNode, JCR_DATA);
                Binary binary = ValueFactoryImpl.getInstance().createBinary(inputStream);
                if (data == null) {
                    qrCodeNode.setProperty(JCR_DATA, binary);
                } else {
                    data.setValue(binary);
                }

                setProperty(qrCodeNode, PROPERTY_FILENAME, fileName);
                setProperty(qrCodeNode, PROPERTY_CONTENTTYPE, MIME_TYPE);
                Calendar calValue = new GregorianCalendar(TimeZone.getDefault());
                setProperty(qrCodeNode, PROPERTY_LASTMODIFIED, calValue);
            } catch (RepositoryException re) {
                LOGGER.error("Could not get Binary. Upload will not be performed", re);
            }
        }
    }

    private void setNodeName(Node node) throws RepositoryException {
        if (node.hasProperty(PN_VANITY_URL) && !node.hasProperty("jcrName")) {
            String newNodeName = _nodeNameHelper.getValidatedName(getNormalizedVanityUrl(node));
            if (!node.getName().equals(newNodeName)) {
                newNodeName = _nodeNameHelper.getUniqueName(node.getParent(), newNodeName);
                NodeUtil.renameNode(node, newNodeName);
            }
        }
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }

    @Inject
    public void setSimpleTranslator(final SimpleTranslator simpleTranslator) {
        _simpleTranslator = simpleTranslator;
    }

    @Inject
    public void setNodeNameHelper(final NodeNameHelper nodeNameHelper) {
        _nodeNameHelper = nodeNameHelper;
    }

    @Inject
    public void setFileSystemHelper(FileSystemHelper fileSystemHelper) {
        _fileSystemHelper = fileSystemHelper;
    }
}
