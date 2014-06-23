package com.aperto.magnolia.vanity;

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


import info.magnolia.test.mock.jcr.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test the service class.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VanityUrlServiceTest {

    private VanityUrlService _service;

    @Test
    public void testRedirectWithNull() throws Exception {
        assertThat(_service.createRedirectUrl(null), equalTo(""));
    }

    @Test
    public void testRedirectWithEmptyNode() throws Exception {
        MockNode mockNode = new MockNode("node");
        assertThat(_service.createRedirectUrl(mockNode), equalTo(""));
    }

    @Test
    public void testRedirectInternalWithAnchor() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", "123-4556-123");
        mockNode.setProperty("linkSuffix", "#anchor1");

        assertThat(_service.createRedirectUrl(mockNode), equalTo("redirect:/internal/page.html#anchor1"));
    }

    @Test
    public void testRedirectExternal() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", "http://www.aperto.de");

        assertThat(_service.createRedirectUrl(mockNode), equalTo("redirect:http://www.aperto.de"));
    }

    @Test
    public void testPublicUrl() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", "123-2454-545");

        assertThat(_service.createPublicUrl(mockNode), equalTo("http://www.airbusgroup.com/page.html"));
    }

    @Before
    public void setUp() throws Exception {
        _service = new VanityUrlService() {
            @Override
            protected String getLinkFromId(final String url) {
                return "/internal/page.html";
            }

            @Override
            protected String getExternalLinkFromId(final String url) {
                return "http://www.airbusgroup.com/page.html";
            }
        };
    }

    @After
    public void tearDown() throws Exception {

    }
}
