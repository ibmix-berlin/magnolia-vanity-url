package com.aperto.magnolia.vanity.app;

import info.magnolia.ui.contentapp.detail.action.AbstractItemActionDefinition;

/**
 * Action definition to open the vanity url preview.
 *
 * @author frank.sommer
 * @since 06.05.14
 */
public class PreviewActionDefinition extends AbstractItemActionDefinition {

    public PreviewActionDefinition() {
        setImplementationClass(PreviewAction.class);
    }
}
