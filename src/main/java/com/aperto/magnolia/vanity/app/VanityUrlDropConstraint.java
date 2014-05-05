package com.aperto.magnolia.vanity.app;

import info.magnolia.ui.workbench.tree.drop.BaseDropConstraint;

/**
 * Implementation of {@link info.magnolia.ui.workbench.tree.drop.DropConstraint} for vanity url app.
 *
 * @author frank.sommer
 * @since 05.05.14
 */
public class VanityUrlDropConstraint extends BaseDropConstraint {

    public VanityUrlDropConstraint() {
        super("mgnl:vanityUrl");
    }
}
