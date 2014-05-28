package com.aperto.magnolia.vanity;

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.ContextFactory;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.context.WebContext;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.test.mock.jcr.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for mapping.
 *
 * @author frank.sommer
 * @since 28.05.14
 */
public class VirtualVanityUriMappingTest {

    private VirtualVanityUriMapping _uriMapping;

    @Test
    public void testRootRequest() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/");
        assertThat(mappingResult, nullValue());
    }

    @Test
    public void testPageRequest() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/home.html");
        assertThat(mappingResult, nullValue());
    }

    @Test
    public void testVanityUrlWithoutTarget() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/home");
        assertThat(mappingResult, nullValue());
    }

    @Test
    public void testVanityUrlWithTarget() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/xmas");
        assertThat(mappingResult, notNullValue());
        assertThat(mappingResult.getToURI(), equalTo("redirect:/internal/page.html"));
    }

    @Before
    public void setUp() throws Exception {
        _uriMapping = new VirtualVanityUriMapping();

        VanityUrlModule module = new VanityUrlModule();
        Map<String, String> excludes = new HashMap<>();
        excludes.put("pages", ".*\\..*");
        module.setExcludes(excludes);
        _uriMapping.setVanityUrlModule(module);

        VanityUrlService vanityUrlService = mock(VanityUrlService.class);
        when(vanityUrlService.queryForVanityUrlNode("/home", "default")).thenReturn(null);

        MockNode mockNode = new MockNode("xmas");
        when(vanityUrlService.queryForVanityUrlNode("/xmas", "default")).thenReturn(mockNode);

        when(vanityUrlService.createRedirectUrl(mockNode)).thenReturn("redirect:/internal/page.html");
        _uriMapping.setVanityUrlService(vanityUrlService);

        initWebContext();

        initComponentProvider();
    }

    private void initComponentProvider() {
        ComponentProvider componentProvider = mock(ComponentProvider.class);
        ContextFactory contextFactory = mock(ContextFactory.class);
        SystemContext systemContext = mock(SystemContext.class);
        when(contextFactory.getSystemContext()).thenReturn(systemContext);
        when(componentProvider.getComponent(ContextFactory.class)).thenReturn(contextFactory);
        Components.setComponentProvider(componentProvider);
    }

    private void initWebContext() {
        WebContext webContext = mock(WebContext.class);
        AggregationState aggregationState = mock(AggregationState.class);
        when(webContext.getAggregationState()).thenReturn(aggregationState);
        MgnlContext.setInstance(webContext);
    }

    @After
    public void tearDown() throws Exception {
        MgnlContext.setInstance(null);
        Components.setComponentProvider(null);
    }
}
