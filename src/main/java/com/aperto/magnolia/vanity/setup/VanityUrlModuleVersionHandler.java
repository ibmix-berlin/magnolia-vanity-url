package com.aperto.magnolia.vanity.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.nodebuilder.task.NodeBuilderTask;

import java.util.ArrayList;
import java.util.List;

import static info.magnolia.jcr.util.NodeTypes.ContentNode;
import static info.magnolia.nodebuilder.Ops.addNode;
import static info.magnolia.nodebuilder.Ops.addProperty;
import static info.magnolia.nodebuilder.task.ErrorHandling.logging;
import static info.magnolia.repository.RepositoryConstants.CONFIG;

/**
 * Module version handler of this magnolia module.
 *
 * @author frank.sommer
 */
public class VanityUrlModuleVersionHandler extends DefaultModuleVersionHandler {

    private final Task _addAppToLauncher = new NodeBuilderTask("Add app to app launcher", "Add vanity url app to app launcher.", logging, CONFIG, "/modules/ui-admincentral/config/appLauncherLayout/groups/manage/apps",
        addNode("vanityUrl", ContentNode.NAME)
    );

    private final Task _addUriRepositoryMapping = new NodeBuilderTask("Add repository mapping", "Add uri to repository mapping for vanityUrls.", logging, CONFIG, "/server/URI2RepositoryMapping/mappings",
        addNode("vanityUrls", ContentNode.NAME).then(
            addProperty("URIPrefix", "/vanityUrls"),
            addProperty("handlePrefix", ""),
            addProperty("repository", "vanityUrls")
        )
    );

    @Override
    protected List<Task> getExtraInstallTasks(final InstallContext installContext) {
        List<Task> tasks = new ArrayList<>();
        tasks.add(_addAppToLauncher);
        tasks.add(_addUriRepositoryMapping);
        return tasks;
    }
}