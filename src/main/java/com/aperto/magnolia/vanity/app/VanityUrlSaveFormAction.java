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
import info.magnolia.cms.core.Path;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.form.action.SaveFormAction;
import info.magnolia.ui.form.action.SaveFormActionDefinition;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.form.field.upload.basic.BasicFileItemWrapper;
import info.magnolia.ui.vaadin.integration.jcr.AbstractJcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import net.glxn.qrgen.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.OutputStream;

import static com.aperto.magnolia.vanity.VanityUrlService.NN_IMAGE;
import static com.google.common.io.Closeables.closeQuietly;
import static info.magnolia.jcr.util.NodeTypes.Resource;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static org.apache.commons.lang.StringUtils.strip;
import static org.apache.commons.lang.StringUtils.trim;

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
    public static final String IMAGE_EXTENSION = ".png";

    private SimpleTranslator _simpleTranslator;
    private VanityUrlService _vanityUrlService;

    public VanityUrlSaveFormAction(final SaveFormActionDefinition definition, final JcrNodeAdapter item, final EditorCallback callback, final EditorValidator validator) {
        super(definition, item, callback, validator);
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validator.isValid()) {
            savePreviewImage();
        }
        super.execute();
    }

    private void savePreviewImage() {
        OutputStream outputStream = null;
        try {
            final Node node = item.applyChanges();
            String url = _vanityUrlService.createPublicUrl(node);
            String fileName = trim(strip(getString(node, "vanityUrl", ""), "/")).replace("/", "-");
            File tmpDirectory = Path.getTempDirectory();

            UploadReceiver uploadReceiver = new UploadReceiver(tmpDirectory, _simpleTranslator);
            outputStream = uploadReceiver.receiveUpload(fileName + IMAGE_EXTENSION, "image/png");
            QRCode.from(url).withSize(QR_WIDTH, GR_HEIGHT).writeTo(outputStream);

            AbstractJcrNodeAdapter itemWithBinaryData = getOrCreateSubItemWithBinaryData();
            BasicFileItemWrapper fileWrapper = new BasicFileItemWrapper(itemWithBinaryData, tmpDirectory);
            fileWrapper.populateFromReceiver(uploadReceiver);
        } catch (RepositoryException e) {
            LOGGER.error("Error on saving preview image for vanity url.", e);
        } finally {
            closeQuietly(outputStream);
        }
    }

    /**
     * Get or Create the Binary Item.
     * If this Item doesn't exist yet, initialize all fields (as Property).
     * Inspired from Magnolia.
     * @see info.magnolia.ui.form.field.factory.BasicUploadFieldFactory#getOrCreateSubItemWithBinaryData()
     */
    private AbstractJcrNodeAdapter getOrCreateSubItemWithBinaryData() {
        AbstractJcrNodeAdapter child = null;
        try {
            Node node = item.getJcrItem();
            if (node.hasNode(NN_IMAGE) && !(item instanceof JcrNewNodeAdapter)) {
                child = new JcrNodeAdapter(node.getNode(NN_IMAGE));
                child.setParent(item);
            } else {
                child = new JcrNewNodeAdapter(node, Resource.NAME, NN_IMAGE);
                child.setParent(item);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Could get or create item", e);
        }
        return child;
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }

    @Inject
    public void setSimpleTranslator(final SimpleTranslator simpleTranslator) {
        _simpleTranslator = simpleTranslator;
    }
}
