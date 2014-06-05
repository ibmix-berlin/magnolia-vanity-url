package com.aperto.magnolia.vanity.app;

import info.magnolia.ui.workbench.column.definition.AbstractColumnDefinition;

/**
 * Definition for link column.
 *
 * @author frank.sommer
 * @since 05.06.14
 */
public class LinkColumnDefinition extends AbstractColumnDefinition {
    public LinkColumnDefinition() {
        setFormatterClass(LinkColumnFormatter.class);
    }
}
