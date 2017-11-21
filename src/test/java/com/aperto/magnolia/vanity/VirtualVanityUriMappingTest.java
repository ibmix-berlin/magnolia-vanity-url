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


import info.magnolia.cms.beans.config.VirtualURIMapping;
import info.magnolia.cms.core.AggregationState;
import info.magnolia.context.ContextFactory;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.jcr.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
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

        Provider moduleProvider = mock(Provider.class);
        VanityUrlModule module = new VanityUrlModule();
        Map<String, String> excludes = new HashMap<>();
        excludes.put("pages", ".*\\..*");
        module.setExcludes(excludes);
        when(moduleProvider.get()).thenReturn(module);
        _uriMapping.setVanityUrlModule(moduleProvider);

        Provider serviceProvider = mock(Provider.class);
        VanityUrlService vanityUrlService = mock(VanityUrlService.class);
        when(vanityUrlService.queryForVanityUrlNode("/home", "default")).thenReturn(null);

        MockNode mockNode = new MockNode("xmas");
        when(vanityUrlService.queryForVanityUrlNode("/xmas", "default")).thenReturn(mockNode);

        when(vanityUrlService.createRedirectUrl(mockNode)).thenReturn("redirect:/internal/page.html");
        when(serviceProvider.get()).thenReturn(vanityUrlService);
        _uriMapping.setVanityUrlService(serviceProvider);

        Provider registryProvider = mock(Provider.class);
        ModuleRegistry moduleRegistry = mock(ModuleRegistry.class);
        when(registryProvider.get()).thenReturn(moduleRegistry);
        _uriMapping.setModuleRegistry(registryProvider);

        initWebContext();

        initComponentProvider();
    }

    private void initComponentProvider() {
        ContextFactory contextFactory = mock(ContextFactory.class);
        SystemContext systemContext = mock(SystemContext.class);
        when(contextFactory.getSystemContext()).thenReturn(systemContext);
        ComponentsTestUtil.setInstance(ContextFactory.class, contextFactory);
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
    }
}
