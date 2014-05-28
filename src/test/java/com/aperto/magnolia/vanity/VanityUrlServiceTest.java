package com.aperto.magnolia.vanity;

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
