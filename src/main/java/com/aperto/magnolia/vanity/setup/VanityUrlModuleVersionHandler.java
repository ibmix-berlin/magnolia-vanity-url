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
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RemovePropertyTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;

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

    private final Task _addAppToLauncher = new NodeBuilderTask("Add app to app launcher", "Add vanity url app to app launcher.", logging, CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps",
        addNode("vanityUrls", ContentNode.NAME)
    );

    private final Task _addUriRepositoryMapping = new NodeBuilderTask("Add repository mapping", "Add uri to repository mapping for vanityUrls.", logging, CONFIG, "/server/URI2RepositoryMapping/mappings",
        addNode(WORKSPACE, ContentNode.NAME).then(
            addProperty("URIPrefix", (Object) ("/" + WORKSPACE)),
            addProperty("handlePrefix", (Object) ""),
            addProperty("repository", (Object) WORKSPACE)
        )
    );

    public VanityUrlModuleVersionHandler() {
        DeltaBuilder update154 = DeltaBuilder.update("1.5.4", "Update to version 1.5.4");
        update154.addTask(new RemovePropertyTask("Remove wrong app activate config", "Remove recursive flag in activate action in app.", CONFIG, "/modules/magnolia-vanity-url/apps/vanityUrl/subApps/browser/actions/activate", "recursive"));
        update154.addTask(new SetPropertyTask("Fix folder delete action config", CONFIG, "/modules/magnolia-vanity-url/apps/vanityUrl/subApps/browser/actions/confirmDeleteFolder", "successActionName", "delete"));
        update154.addTask(new RemoveNodeTask("Remove wrong app action config", "Remove deleteFolder action in app.", CONFIG, "/modules/magnolia-vanity-url/apps/vanityUrl/subApps/browser/actions/deleteFolder"));
        register(update154);

        DeltaBuilder update160 = DeltaBuilder.update("1.6.0", "Update to version 1.6.0");
        update160.addTask(new RemoveNodeTask("Remove jcr uri mapping config", "Remove virtual uri mapping in jcr. It's in yaml now.", CONFIG, "/modules/magnolia-vanity-url/virtualURIMapping"));
        update160.addTask(new RemoveNodeTask("Remove jcr field types config", "Remove field types in jcr. No need for field types anymore.", CONFIG, "/modules/magnolia-vanity-url/fieldTypes"));
        update160.addTask(new RemoveNodeTask("Remove jcr app config", "Remove app in jcr. It's in yaml now.", CONFIG, "/modules/magnolia-vanity-url/apps"));
        update160.addTask(new RemoveNodeTask("Remove old app in app launcher", "Remove old app in jcr from app launcher config.", CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps/vanityUrl"));
        update160.addTask(_addAppToLauncher);
        register(update160);
    }

    @Override
    protected List<Task> getExtraInstallTasks(final InstallContext installContext) {
        List<Task> tasks = new ArrayList<>();
        tasks.add(_addAppToLauncher);
        tasks.add(_addUriRepositoryMapping);
        return tasks;
    }
}
