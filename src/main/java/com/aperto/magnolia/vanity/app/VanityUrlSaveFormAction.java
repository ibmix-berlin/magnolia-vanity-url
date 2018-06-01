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
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.FileSystemHelper;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeNameHelper;
import info.magnolia.jcr.util.NodeTypes.Resource;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.form.action.SaveFormAction;
import info.magnolia.ui.form.action.SaveFormActionDefinition;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
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
import java.util.TimeZone;

import static com.aperto.magnolia.vanity.VanityUrlService.NN_IMAGE;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static info.magnolia.jcr.util.PropertyUtil.getPropertyOrNull;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static info.magnolia.jcr.util.PropertyUtil.setProperty;
import static org.apache.commons.io.IOUtils.closeQuietly;
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
public class VanityUrlSaveFormAction extends SaveFormAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanityUrlSaveFormAction.class);
    private static final int QR_WIDTH = 500;
    private static final int GR_HEIGHT = 500;
    private static final String MIME_TYPE = "image/png";
    public static final String IMAGE_EXTENSION = ".png";

    private SimpleTranslator _simpleTranslator;
    private VanityUrlService _vanityUrlService;
    private NodeNameHelper _nodeNameHelper;

    public VanityUrlSaveFormAction(final SaveFormActionDefinition definition, final JcrNodeAdapter item, final EditorCallback callback, final EditorValidator validator) {
        super(definition, item, callback, validator);
    }

    @Override
    public void execute() throws ActionExecutionException {
        super.execute();
        if (validator.isValid()) {
            savePreviewImage();
        }
    }

    private String getNormalizedVanityUrl(final Node node) {
        String vanityUrl = getString(node, PN_VANITY_URL);
        vanityUrl = stripStart(trimToEmpty(vanityUrl), "/");
        return vanityUrl;
    }

    private void savePreviewImage() {
        FileOutputStream outputStream = null;
        FileInputStream qrCodeInputStream = null;
        try {
            final Node node = item.applyChanges();
            String url = _vanityUrlService.createVanityUrl(node);
            String fileName = trim(strip(getString(node, PN_VANITY_URL, ""), "/")).replace("/", "-");
            File tmpQrCodeFile = Components.getComponent(FileSystemHelper.class).getTempDirectory();

            UploadReceiver uploadReceiver = new UploadReceiver(tmpQrCodeFile, _simpleTranslator);
            outputStream = (FileOutputStream) uploadReceiver.receiveUpload(fileName + IMAGE_EXTENSION, MIME_TYPE);
            QRCode.from(url).withSize(QR_WIDTH, GR_HEIGHT).writeTo(outputStream);

            Node qrNode;
            if (node.hasNode(NN_IMAGE)) {
                qrNode = node.getNode(NN_IMAGE);
            } else {
                qrNode = node.addNode(NN_IMAGE, Resource.NAME);
            }

            qrCodeInputStream = new FileInputStream(uploadReceiver.getFile());
            populateItem(qrCodeInputStream, qrNode, fileName);
            outputStream.flush();
        } catch (RepositoryException e) {
            LOGGER.error("Error on saving preview image for vanity url.", e);
        } catch (IOException e) {
            LOGGER.error("Error handling qr image file.", e);
        } finally {
            closeQuietly(outputStream);
            closeQuietly(qrCodeInputStream);
        }
    }

    private void populateItem(InputStream inputStream, Node qrCodeNode, final String fileName) throws RepositoryException {
        if (inputStream != null) {
            try {
                Property data = getPropertyOrNull(qrCodeNode, JCR_DATA);
                Binary binary = ValueFactoryImpl.getInstance().createBinary(inputStream);
                if (data == null) {
                    qrCodeNode.setProperty(JCR_DATA, binary);
                } else {
                    data.setValue(binary);
                }

                setProperty(qrCodeNode, FileProperties.PROPERTY_FILENAME, fileName);
                setProperty(qrCodeNode, FileProperties.PROPERTY_CONTENTTYPE, MIME_TYPE);
                Calendar calValue = new GregorianCalendar(TimeZone.getDefault());
                setProperty(qrCodeNode, FileProperties.PROPERTY_LASTMODIFIED, calValue);
            } catch (RepositoryException re) {
                LOGGER.error("Could not get Binary. Upload will not be performed", re);
            }
        }
    }

    protected void setNodeName(Node node, JcrNodeAdapter item) throws RepositoryException {
        if (node.hasProperty(PN_VANITY_URL) && !node.hasProperty("jcrName")) {
            String newNodeName = _nodeNameHelper.getValidatedName(getNormalizedVanityUrl(node));
            if (!node.getName().equals(newNodeName)) {
                newNodeName = _nodeNameHelper.getUniqueName(node.getParent(), newNodeName);
                item.setNodeName(newNodeName);
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
}
