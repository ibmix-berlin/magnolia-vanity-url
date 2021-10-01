package com.aperto.magnolia.vanity.app;

/*
 * #%L
 * magnolia-vanity-url Magnolia Module
 * %%
 * Copyright (C) 2013 - 2021 Aperto – An IBM Company
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


import com.machinezoo.noexception.Exceptions;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.contentapp.column.jcr.JcrTitleColumnDefinition;
import info.magnolia.ui.contentapp.configuration.column.ColumnType;

import javax.jcr.Item;
import javax.jcr.Node;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Column definition for vanity url column.
 *
 * @author frank.sommer
 * @since 1.6.0
 */
@ColumnType("vanityUrlColumn")
public class VanityUrlColumnDefinition extends JcrTitleColumnDefinition {

    public VanityUrlColumnDefinition() {
        setValueProvider(ValueProvider.class);
    }

    /**
     * Value provider.
     *
     * @author frank.sommer
     * @since 1.6.0
     */
    public static class ValueProvider extends JcrTitleValueProvider {
        public ValueProvider(VanityUrlColumnDefinition definition) {
            super(definition);
        }

        public String apply(Item item) {
            return Exceptions.wrap().get(() -> {
                Node node = (Node) item;
                if (!NodeUtil.isNodeType(node, NodeTypes.Folder.NAME) && !NodeUtil.hasMixin(node, NodeTypes.Deleted.NAME)) {
                    String vanityUrl = PropertyUtil.getString(node, getDefinition().getName());
                    if (isBlank(vanityUrl)) {
                        vanityUrl = item.getName();
                    }

                    return "<span class=\"v-table-icon-element " + getIcon(item) + "\" ></span>" + vanityUrl;
                } else {
                    return super.apply(item);
                }
            });
        }

        public VanityUrlColumnDefinition getDefinition() {
            return (VanityUrlColumnDefinition) super.getDefinition();
        }
    }
}