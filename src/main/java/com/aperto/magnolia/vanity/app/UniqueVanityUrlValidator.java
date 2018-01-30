package com.aperto.magnolia.vanity.app;

/*
 * #%L
 * magnolia-vanity-url Magnolia Module
 * %%
 * Copyright (C) 2013 - 2018 Aperto AG
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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.validator.AbstractStringValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.jcr.Node;
import java.util.List;

import static com.aperto.magnolia.vanity.VanityUrlService.PN_SITE;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static info.magnolia.jcr.util.NodeUtil.getNodeIdentifierIfPossible;

/**
 * Validator for unique vanity urls.
 *
 * @author frank.sommer
 * @since 24.01.2018
 */
public class UniqueVanityUrlValidator extends AbstractStringValidator {

    private final Item _item;
    private final VanityUrlService _vanityUrlService;

    public UniqueVanityUrlValidator(final UniqueVanityUrlValidatorDefinition definition, final Item item, final VanityUrlService vanityUrlService) {
        super(definition.getErrorMessage());
        _item = item;
        _vanityUrlService = vanityUrlService;
    }

    @Override
    protected boolean isValidValue(final String value) {
        boolean isValid = true;
        if (_item instanceof JcrNodeAdapter) {
            JcrNodeAdapter jcrNodeAdapter = (JcrNodeAdapter) _item;
            isValid = validateField(jcrNodeAdapter);
        }
        return isValid;
    }

    private boolean validateField(final JcrNodeAdapter jcrNodeAdapter) {
        boolean validField = true;
        Property<?> vanityUrl = jcrNodeAdapter.getItemProperty(PN_VANITY_URL);
        Property<?> site = jcrNodeAdapter.getItemProperty(PN_SITE);
        if (vanityUrl.getValue() != null && site.getValue() != null) {
            List<Node> nodes = _vanityUrlService.queryForVanityUrlNodes(vanityUrl.getValue().toString(), site.getValue().toString());
            if (jcrNodeAdapter instanceof JcrNewNodeAdapter) {
                validField = nodes.isEmpty();
            } else {
                String currentIdentifier = getNodeIdentifierIfPossible(jcrNodeAdapter.getJcrItem());
                for (Node node : nodes) {
                    if (!currentIdentifier.equals(getNodeIdentifierIfPossible(node))) {
                        validField = false;
                        break;
                    }
                }
            }
        }
        return validField;
    }
}
