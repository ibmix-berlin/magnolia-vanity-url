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
import com.vaadin.v7.data.Property;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

import static com.aperto.magnolia.vanity.VanityUrlService.PN_SITE;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test unique validator.
 *
 * @author frank.sommer
 * @since 30.01.2018
 */
public class UniqueVanityUrlValidatorTest {

    private VanityUrlService _vanityUrlService;

    @Before
    public void setUp() throws Exception {
        _vanityUrlService = mock(VanityUrlService.class);
        when(_vanityUrlService.queryForVanityUrlNodes("/new", "site")).thenReturn(emptyList());

        List<Node> uniqueList = new ArrayList<>();
        Node node = mock(Node.class);
        when(node.getIdentifier()).thenReturn("123");
        uniqueList.add(node);
        when(_vanityUrlService.queryForVanityUrlNodes("/unique", "site")).thenReturn(uniqueList);

        when(_vanityUrlService.queryForVanityUrlNodes("/lorem", "site")).thenReturn(uniqueList);
    }

    @Test
    public void isNewValue() throws Exception {
        JcrNewNodeAdapter item = mock(JcrNewNodeAdapter.class);
        customizeItem(item);
        UniqueVanityUrlValidator uniqueValidator = new UniqueVanityUrlValidator(new UniqueVanityUrlValidatorDefinition(), item, _vanityUrlService);
        assertTrue(uniqueValidator.isValidValue("/new"));
    }

    @Test
    public void isUniqueValue() throws Exception {
        JcrNodeAdapter item = mock(JcrNodeAdapter.class);
        customizeItem(item);

        Node currentNode = mock(Node.class);
        when(currentNode.getIdentifier()).thenReturn("123");
        when(item.getJcrItem()).thenReturn(currentNode);

        UniqueVanityUrlValidator uniqueValidator = new UniqueVanityUrlValidator(new UniqueVanityUrlValidatorDefinition(), item, _vanityUrlService);
        assertTrue(uniqueValidator.isValidValue("/unique"));
    }

    @Test
    public void isExistingValue() throws Exception {
        JcrNodeAdapter item = mock(JcrNodeAdapter.class);
        customizeItem(item);

        Node currentNode = mock(Node.class);
        when(currentNode.getIdentifier()).thenReturn("234");
        when(item.getJcrItem()).thenReturn(currentNode);

        UniqueVanityUrlValidator uniqueValidator = new UniqueVanityUrlValidator(new UniqueVanityUrlValidatorDefinition(), item, _vanityUrlService);
        assertFalse(uniqueValidator.isValidValue("/lorem"));
    }

    private void customizeItem(final JcrNodeAdapter item) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getValue()).thenReturn("site");
        when(item.getItemProperty(PN_SITE)).thenReturn(property);
    }
}