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


import com.aperto.magnolia.vanity.VanityUrlService;
import com.machinezoo.noexception.Exceptions;
import com.vaadin.data.ValueProvider;

import javax.inject.Inject;
import javax.jcr.Item;
import javax.jcr.Node;

/**
 * Vanity url link column value provider.
 *
 * @author frank.sommer
 * @since 1.6.0
 */
public class VanityLinkProvider implements ValueProvider<Item, String> {
    private final VanityUrlService _vanityUrlService;

    @Inject
    public VanityLinkProvider(VanityUrlService vanityUrlService) {
        _vanityUrlService = vanityUrlService;
    }

    @Override
    public String apply(Item item) {
        return Exceptions.wrap().get(() -> _vanityUrlService.createPublicUrl((Node) item));
    }
}