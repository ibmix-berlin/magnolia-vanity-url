package com.aperto.magnolia.vanity.app;

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


import info.magnolia.ui.workbench.tree.drop.BaseDropConstraint;

/**
 * Implementation of {@link info.magnolia.ui.workbench.tree.drop.DropConstraint} for vanity url app.
 *
 * @author frank.sommer
 * @since 05.05.14
 */
public class VanityUrlDropConstraint extends BaseDropConstraint {

    public VanityUrlDropConstraint() {
        super("mgnl:vanityUrl");
    }
}
