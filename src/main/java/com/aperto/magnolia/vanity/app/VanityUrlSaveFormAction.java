package com.aperto.magnolia.vanity.app;

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
import net.glxn.qrgen.image.ImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.OutputStream;

import static com.google.common.io.Closeables.closeQuietly;
import static info.magnolia.dam.DamConstants.CONTENT_NODE_NAME;
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
    private static final int PREVIEW_WIDTH = 160;
    private static final int PREVIEW_HEIGHT = 160;

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
            outputStream = uploadReceiver.receiveUpload(fileName + ".jpg", "image/jpeg");
            QRCode.from(url).to(ImageType.JPG).withSize(PREVIEW_WIDTH, PREVIEW_HEIGHT).writeTo(outputStream);

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
            if (node.hasNode(CONTENT_NODE_NAME) && !(item instanceof JcrNewNodeAdapter)) {
                child = new JcrNodeAdapter(node.getNode(CONTENT_NODE_NAME));
                child.setParent(item);
            } else {
                child = new JcrNewNodeAdapter(node, Resource.NAME, CONTENT_NODE_NAME);
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
