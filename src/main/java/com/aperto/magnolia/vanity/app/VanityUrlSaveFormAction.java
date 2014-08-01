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


import static com.aperto.magnolia.vanity.VanityUrlService.NN_IMAGE;
import static info.magnolia.jcr.util.PropertyUtil.getString;
import static org.apache.commons.lang.StringUtils.strip;
import static org.apache.commons.lang.StringUtils.trim;
import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.Path;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeTypes.Resource;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.form.action.SaveFormAction;
import info.magnolia.ui.form.action.SaveFormActionDefinition;
import info.magnolia.ui.form.field.upload.UploadReceiver;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import net.glxn.qrgen.QRCode;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aperto.magnolia.vanity.VanityUrlService;

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
    private String _fileName;

    public VanityUrlSaveFormAction(final SaveFormActionDefinition definition, final JcrNodeAdapter item, final EditorCallback callback, final EditorValidator validator) {
        super(definition, item, callback, validator);
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (validator.isValid()) {
            try {
                savePreviewImage();
            } catch (IOException e) {
                LOGGER.error("Error while saving vanity url", e);
            }
        }
        super.execute();
    }

    private void savePreviewImage() throws IOException {
        FileOutputStream outputStream = null;
        FileInputStream qrCodeInputStream = null;
        try {
            final Node node = item.applyChanges();
            String url = _vanityUrlService.createVanityUrl(node);
            _fileName = trim(strip(getString(node, "vanityUrl", ""), "/")).replace("/", "-");
            File tmpQrCodeFile = Path.getTempDirectory();

            UploadReceiver uploadReceiver = new UploadReceiver(tmpQrCodeFile, _simpleTranslator);
            outputStream = (FileOutputStream) uploadReceiver.receiveUpload(_fileName + IMAGE_EXTENSION, "image/png");
            QRCode.from(url).withSize(QR_WIDTH, GR_HEIGHT).writeTo(outputStream);
            Node qrNode = node.addNode(NN_IMAGE, Resource.NAME);
            
           
            qrCodeInputStream = new FileInputStream(uploadReceiver.getFile());
            populateItem(qrCodeInputStream, qrNode);
            outputStream.flush();
            
        } catch (RepositoryException e) {
            LOGGER.error("Error on saving preview image for vanity url.", e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (qrCodeInputStream != null) {
                qrCodeInputStream.close();
            }
        }
    }
    
    protected void populateItem(InputStream inputStream, Node qrCodeNode) throws  RepositoryException {
        Property data = PropertyUtil.getPropertyOrNull(qrCodeNode, JcrConstants.JCR_DATA);
        

        if (inputStream != null) {
            try {
                if (data == null) {
                    data = qrCodeNode.setProperty(JcrConstants.JCR_DATA, ValueFactoryImpl.getInstance().createBinary(inputStream));
                } else {
                    data.setValue(ValueFactoryImpl.getInstance().createBinary(inputStream));
                }
                
            } catch (Exception re) {
                LOGGER.error("Could not get Binary. Upload will not be performed", re);
                return;
            }
        }
        PropertyUtil.setProperty(qrCodeNode, FileProperties.PROPERTY_FILENAME, _fileName);

        PropertyUtil.setProperty(qrCodeNode, FileProperties.PROPERTY_CONTENTTYPE, "image/png");
           
        Calendar  calValue = new GregorianCalendar(TimeZone.getDefault());

        PropertyUtil.setProperty(qrCodeNode, FileProperties.PROPERTY_LASTMODIFIED, calValue);
  
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
