package com.aperto.magnolia.vanity;

import java.util.Map;

/**
 * Module class of this module.
 *
 * @author frank.sommer
 * @since 26.01.2012
 */
public class VanityUrlModule {
    private Map<String, String> _excludes;

    public Map<String, String> getExcludes() {
        return _excludes;
    }

    public void setExcludes(Map<String, String> excludes) {
        _excludes = excludes;
    }
}