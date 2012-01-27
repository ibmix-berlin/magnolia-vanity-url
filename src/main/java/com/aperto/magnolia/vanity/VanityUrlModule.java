package com.aperto.magnolia.vanity;

import java.util.Map;

/**
 * Module class of this module.
 *
 * @author frank.sommer
 * @since 26.01.2012
 */
public class VanityUrlModule {
    private static VanityUrlModule c_instance;
    private Map<String, String> _excludes;

    public VanityUrlModule() {
        c_instance = this;
    }

    public static VanityUrlModule getInstance() {
        return c_instance;
    }

    public Map<String, String> getExcludes() {
        return _excludes;
    }

    public void setExcludes(Map<String, String> excludes) {
        _excludes = excludes;
    }
}