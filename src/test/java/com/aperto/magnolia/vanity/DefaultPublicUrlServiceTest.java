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

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.test.mock.jcr.MockNode;
import org.junit.Before;
import org.junit.Test;

import static com.aperto.magnolia.vanity.VanityUrlService.PN_LINK;
import static com.aperto.magnolia.vanity.VanityUrlService.PN_VANITY_URL;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for default public url service ({@link DefaultPublicUrlService}).
 *
 * @author frank.sommer
 * @since 16.10.14
 */
public class DefaultPublicUrlServiceTest {
    private DefaultPublicUrlService _service;

    @Test
    public void testExternalTarget() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty(PN_VANITY_URL, "/aperto");
        mockNode.setProperty(PN_LINK, "http://www.aperto.de");

        assertThat(_service.createTargetUrl(mockNode), equalTo("http://www.aperto.de"));
        assertThat(_service.createVanityUrl(mockNode), equalTo("http://www.aperto.de/aperto"));
    }

    @Test
    public void testInternalTarget() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty(PN_VANITY_URL, "/aperto");
        mockNode.setProperty(PN_LINK, "123-456-789");

        assertThat(_service.createTargetUrl(mockNode), equalTo("http://www.aperto.de/context/page.html"));
        assertThat(_service.createVanityUrl(mockNode), equalTo("http://www.aperto.de/aperto"));
    }

    @Test
    public void testExternalTargetWithConfiguredTargetContextPath() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty(PN_VANITY_URL, "/aperto");
        mockNode.setProperty(PN_LINK, "http://www.aperto.de");
        _service.setTargetContextPath("/public");

        assertThat(_service.createTargetUrl(mockNode), equalTo("http://www.aperto.de"));
        assertThat(_service.createVanityUrl(mockNode), equalTo("http://www.aperto.de/public/aperto"));
    }

    @Test
    public void testInternalTargetWithConfiguredTargetContextPath() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty(PN_VANITY_URL, "/aperto");
        mockNode.setProperty(PN_LINK, "123-456-789");
        _service.setTargetContextPath("/public");

        assertThat(_service.createTargetUrl(mockNode), equalTo("http://www.aperto.de/context/page.html"));
        assertThat(_service.createVanityUrl(mockNode), equalTo("http://www.aperto.de/public/aperto"));
    }

    @Before
    public void setUp() {
        _service = new DefaultPublicUrlService() {
            @Override
            protected String getExternalLinkFromId(final String nodeId) {
                return "http://www.aperto.de/context/page.html";
            }
        };

        ServerConfiguration serverConfiguration = mock(ServerConfiguration.class);
        when(serverConfiguration.getDefaultBaseUrl()).thenReturn("http://www.aperto.de/author");
        _service.setServerConfiguration(serverConfiguration);
        _service.setContextPath("/author");
    }
}
