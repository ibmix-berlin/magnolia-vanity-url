package com.aperto.magnolia.vanity.app;

import com.aperto.magnolia.vanity.VanityUrlService;
import com.vaadin.ui.Table;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;

import javax.inject.Inject;
import javax.jcr.Item;
import javax.jcr.Node;

/**
 * Formatter for link column.
 *
 * @author frank.sommer
 * @since 05.06.14
 */
public class LinkColumnFormatter extends AbstractColumnFormatter<LinkColumnDefinition> {

    private VanityUrlService _vanityUrlService;

    public LinkColumnFormatter(final LinkColumnDefinition definition) {
        super(definition);
    }

    @Override
    public Object generateCell(final Table source, final Object itemId, final Object columnId) {
        String link = "";

        final Item jcrItem = getJcrItem(source, itemId);
        if (jcrItem != null && jcrItem.isNode()) {
            link = _vanityUrlService.createPublicUrl((Node) jcrItem);
        }

        return link;
    }

    @Inject
    public void setVanityUrlService(final VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }
}
