package com.aperto.magnolia.vanity.app;

/*
 * #%L
 * magnolia-vanity-url Magnolia Module
 * %%
 * Copyright (C) 2013 - 2015 Aperto AG
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


import com.vaadin.ui.Table;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.aperto.magnolia.vanity.VanityUrlModule.NT_VANITY;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static info.magnolia.jcr.util.NodeUtil.isNodeType;
import static info.magnolia.jcr.util.PropertyUtil.getString;

/**
 * Column formatter which respects vanity url and folder nodes.
 *
 * @author frank.sommer
 * @since 1.3.3, 1.4.0
 */
public class FolderUrlNameColumnFormatter extends AbstractColumnFormatter<PropertyColumnDefinition> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderUrlNameColumnFormatter.class);

    public FolderUrlNameColumnFormatter(PropertyColumnDefinition definition) {
        super(definition);
    }

    public Object generateCell(Table source, Object itemId, Object columnId) {
        String cell = "";
        Item jcrItem = getJcrItem(source, itemId);
        if ((jcrItem != null) && (jcrItem.isNode())) {
            Node node = (Node) jcrItem;
            try {
                if (isNodeType(node, NT_VANITY)) {
                    cell = getString(node, PN_VANITY_URL, "");
                } else {
                    cell = node.getName();
                }
            } catch (RepositoryException e) {
                LOGGER.warn("Unable to get the displayed value for the name column.", e);
            }
        }
        return cell;
    }
}