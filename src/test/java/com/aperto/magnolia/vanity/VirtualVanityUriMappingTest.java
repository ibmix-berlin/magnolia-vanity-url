package com.aperto.magnolia.vanity;

import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.ContextFactory;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.context.WebContext;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.templating.functions.TemplatingFunctions;
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
import static org.mockito.Matchers.anyString;
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
    public void testVanityUrlWithExternalTarget() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/extern");
        assertThat(mappingResult, notNullValue());
        assertThat(mappingResult.getToURI(), equalTo("redirect:http://www.aperto.de"));
    }

    @Test
    public void testVanityUrlWithInternalTarget() {
        VirtualURIMapping.MappingResult mappingResult = _uriMapping.mapURI("/intern");
        assertThat(mappingResult, notNullValue());
        assertThat(mappingResult.getToURI(), equalTo("redirect:/internal/page.html#anchor1"));
    }

    @Before
    public void setUp() throws Exception {
        _uriMapping = new VirtualVanityUriMapping();

        VanityUrlModule module = new VanityUrlModule();
        Map<String, String> excludes = new HashMap<>();
        excludes.put("pages", ".*\\..*");
        module.setExcludes(excludes);
        _uriMapping.setVanityUrlModule(module);

        VanityQueryService queryService = mock(VanityQueryService.class);
        when(queryService.queryForVanityUrlNode("/home", "default")).thenReturn(null);

        MockNode mockNode = new MockNode("external");
        mockNode.setProperty("link", "http://www.aperto.de");
        when(queryService.queryForVanityUrlNode("/extern", "default")).thenReturn(mockNode);

        MockNode mockNode2 = new MockNode("internal");
        mockNode2.setProperty("link", "123-4556-123");
        mockNode2.setProperty("linkSuffix", "#anchor1");
        when(queryService.queryForVanityUrlNode("/intern", "default")).thenReturn(mockNode2);
        _uriMapping.setVanityQueryService(queryService);

        TemplatingFunctions templatingFunctions = mock(TemplatingFunctions.class);
        when(templatingFunctions.link(anyString(), anyString())).thenReturn("/internal/page.html");
        _uriMapping.setTemplatingFunctions(templatingFunctions);

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
