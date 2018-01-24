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
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import javax.inject.Inject;

/**
 * Validator factory.
 *
 * @author frank.sommer
 * @since 24.01.2018
 */
public class UniqueVanityUrlValidatorFactory extends AbstractFieldValidatorFactory<UniqueVanityUrlValidatorDefinition> {

    private final Item _item;
    private final VanityUrlService _vanityUrlService;

    @Inject
    public UniqueVanityUrlValidatorFactory(final UniqueVanityUrlValidatorDefinition definition, final Item item, final VanityUrlService vanityUrlService) {
        super(definition);
        _item = item;
        _vanityUrlService = vanityUrlService;
    }

    @Override
    public Validator createValidator() {
        return new UniqueVanityUrlValidator(definition, _item, _vanityUrlService);
    }
}
