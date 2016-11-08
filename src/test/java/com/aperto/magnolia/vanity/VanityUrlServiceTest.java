package com.aperto.magnolia.vanity;

import info.magnolia.cms.i18n.*;
import info.magnolia.context.*;
import info.magnolia.test.*;
import info.magnolia.test.mock.*;

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
import info.magnolia.test.mock.jcr.*;

import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.aperto.magnolia.vanity.VanityUrlService.NN_IMAGE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import info.magnolia.repository.*;

/**
 * Test the service class.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VanityUrlServiceTest {

    private static final String TEST_UUID = "123-4556-123";
    private static final String TEST_UUID_FORWARD = "123-4556-124";
    
    private VanityUrlService _service;

    @Test
    public void testRedirectWithNull() throws Exception {
        assertThat(_service.createRedirectUrl(null), equalTo(""));
    }

    @Test
    public void testTargetUrlWithEmptyNode() throws Exception {
        MockNode mockNode = new MockNode("node");
        assertThat(_service.createRedirectUrl(mockNode), equalTo(""));
        assertThat(_service.createPreviewUrl(mockNode), equalTo(""));
    }

    @Test
    public void testTargetUrlInternalWithAnchor() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", TEST_UUID);
        mockNode.setProperty("linkSuffix", "#anchor1");

        assertThat(_service.createRedirectUrl(mockNode), equalTo("redirect:/internal/page.html#anchor1"));
        assertThat(_service.createPreviewUrl(mockNode), equalTo("/internal/page.html#anchor1"));
    }

    @Test
    public void testTargetUrlExternal() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", "http://www.aperto.de");

        assertThat(_service.createRedirectUrl(mockNode), equalTo("redirect:http://www.aperto.de"));
        assertThat(_service.createPreviewUrl(mockNode), equalTo("http://www.aperto.de"));
    }

    @Test
    public void testForward() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", TEST_UUID_FORWARD);
        mockNode.setProperty("type", "forward");
        assertThat(_service.createRedirectUrl(mockNode), equalTo("forward:/internal/forward/page.html"));
    }

    @Test
    public void testForwardWithAnchor() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", TEST_UUID_FORWARD);
        mockNode.setProperty("type", "forward");
        mockNode.setProperty("linkSuffix", "#anchor1");
        assertThat(_service.createRedirectUrl(mockNode), equalTo("forward:/internal/forward/page.html#anchor1"));
    }

    @Test
    public void testForwardWithInvalidUrl() throws Exception {
        MockNode mockNode = new MockNode("node");
        mockNode.setProperty("link", "http://www.aperto.de");
        mockNode.setProperty("type", "forward");
        assertThat(_service.createRedirectUrl(mockNode), equalTo(""));
    }

    @Test
    public void testPublicUrl() throws Exception {
        assertThat(_service.createPublicUrl(null), equalTo("http://www.aperto.de/page.html"));
    }

    @Test
    public void testVanityUrl() throws Exception {
        assertThat(_service.createVanityUrl(null), equalTo("http://www.aperto.de/vanity"));
    }

    @Test
    public void testImageLinkWithNull() throws Exception {
        assertThat(_service.createImageLink(null), equalTo(""));
    }

    @Test
    public void testImageLinkWithMissingImage() throws Exception {
        MockNode mockNode = new MockNode("node");
        assertThat(_service.createImageLink(mockNode), equalTo(""));
    }

    @Test
    public void testImageLinkWithImage() throws Exception {
        MockNode mockNode = new MockNode("node");
        MockNode image = new MockNode(NN_IMAGE);
        mockNode.addNode(image);
        assertThat(_service.createImageLink(mockNode), equalTo("/node/qrCode.png"));
    }

    private MockNode createNode(MockSession session, String path) throws Exception {
        MockNode current = (MockNode) session.getRootNode();
        String[] parts   = path.split("/");
        for (String part : parts) {
            MockNode child = null;
            if (current.hasNode(part)) {
                child = (MockNode) current.getNode(part);
            } else {
                child = new MockNode(part);
                current.addNode(child);
            }
            current = child;
        }
        return current;
    }
    
    @Before
    public void setUp() throws Exception {
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
        MockWebContext webContext = new MockWebContext();
        MockSession session = new MockSession(RepositoryConstants.WEBSITE);
        createNode(session, "/internal/forward/page").setIdentifier(TEST_UUID_FORWARD);
        createNode(session, "/internal/page").setIdentifier(TEST_UUID);
        webContext.addSession(RepositoryConstants.WEBSITE, session);
        MgnlContext.setInstance(webContext);
        _service = new VanityUrlService() {

            @Override
            protected String getLinkFromNode(final Node node) {
                String link = "";
                if (node != null) {
                  try {
                      link = node.getPath() + ".html";
                  } catch (RepositoryException e) {
                      // should not happen
                  }
                }
                return link;
            }

        };

        VanityUrlModule vanityUrlModule = new VanityUrlModule();
        PublicUrlService publicUrlService = mock(PublicUrlService.class);
        when(publicUrlService.createTargetUrl((Node) any())).thenReturn("http://www.aperto.de/page.html");
        when(publicUrlService.createVanityUrl((Node) any())).thenReturn("http://www.aperto.de/vanity");
        vanityUrlModule.setPublicUrlService(publicUrlService);
        _service.setVanityUrlModule(vanityUrlModule);
    }
}
