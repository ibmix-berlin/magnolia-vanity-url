package com.aperto.magnolia.vanity.setup;

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


import info.magnolia.jcr.nodebuilder.task.NodeBuilderTask;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.*;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

import static com.aperto.magnolia.vanity.VanityUrlModule.WORKSPACE;
import static info.magnolia.jcr.nodebuilder.Ops.addNode;
import static info.magnolia.jcr.nodebuilder.Ops.addProperty;
import static info.magnolia.jcr.nodebuilder.task.ErrorHandling.logging;
import static info.magnolia.jcr.util.NodeTypes.ContentNode;
import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Module version handler of this magnolia module.
 *
 * @author frank.sommer
 */
public class VanityUrlModuleVersionHandler extends DefaultModuleVersionHandler {
    private static final String FLUSH_CACHE_PATH = "/modules/cache/config/configurations/default/flushPolicy/policies/flushAll/repositories";

    private final Task _addAppToLauncher = new NodeBuilderTask("Add app to app launcher", "Add vanity url app to app launcher.", logging, CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps",
        addNode("vanityUrl", ContentNode.NAME)
    );

    private final Task _addUriRepositoryMapping = new NodeBuilderTask("Add repository mapping", "Add uri to repository mapping for vanityUrls.", logging, CONFIG, "/server/URI2RepositoryMapping/mappings",
        addNode(WORKSPACE, ContentNode.NAME).then(
            addProperty("URIPrefix", "/" + WORKSPACE),
            addProperty("handlePrefix", ""),
            addProperty("repository", WORKSPACE)
        )
    );

    private final Task _addCacheFlushConfig = new PropertyExistsDelegateTask("Check cache flush", "Check cache flush config and add missing configuration.",
        RepositoryConstants.CONFIG, FLUSH_CACHE_PATH, WORKSPACE, null,
        new NodeBuilderTask("Add flush config", "Add flush configuration for vanity url workspace.", logging, CONFIG, FLUSH_CACHE_PATH,
            addProperty(WORKSPACE, WORKSPACE)
        )
    );

    public VanityUrlModuleVersionHandler() {
        DeltaBuilder update131 = DeltaBuilder.update("1.3.1", "Update to version 1.3.1");
        update131.addTask(new BootstrapConditionally("Bootstrap new config", "Bootstrap new public url service configuration.", "/mgnl-bootstrap/magnolia-vanity-url/config.modules.magnolia-vanity-url.config.publicUrlService.xml"));
        register(update131);
        
        DeltaBuilder update133 = DeltaBuilder.update("1.3.3", "Update to version 1.3.3");
        update133.addTask(new BootstrapSingleModuleResource("Bootstrap new config", "Bootstrap folder/type definition in app.", "config.modules.magnolia-vanity-url.apps.vanityUrl.xml"));
        register(update133);
    }

    @Override
    protected List<Task> getExtraInstallTasks(final InstallContext installContext) {
        List<Task> tasks = new ArrayList<>();
        tasks.add(_addAppToLauncher);
        tasks.add(_addUriRepositoryMapping);
        tasks.add(_addCacheFlushConfig);
        return tasks;
    }
}