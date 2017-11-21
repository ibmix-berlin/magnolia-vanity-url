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
import com.vaadin.v7.ui.Table;

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
