package com.aperto.magnolia.vanity.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.nodebuilder.NodeOperation;
import info.magnolia.nodebuilder.task.NodeBuilderTask;

import java.util.ArrayList;
import java.util.List;

import static info.magnolia.cms.beans.config.ContentRepository.CONFIG;
import static info.magnolia.cms.core.ItemType.CONTENTNODE;
import static info.magnolia.nodebuilder.Ops.addNode;
import static info.magnolia.nodebuilder.Ops.addProperty;
import static info.magnolia.nodebuilder.Ops.getNode;
import static info.magnolia.nodebuilder.task.ErrorHandling.logging;

/**
 * Module version handler of this magnolia module.
 *
 * @author frank.sommer
 */
public class VanityUrlModuleVersionHandler extends DefaultModuleVersionHandler {
    private static final String STANDARD_TEMPLATING_KIT = "standard-templating-kit";

    private static final NodeOperation VANITY_OPS = addNode("vanityUrl", CONTENTNODE.getSystemName()).then(
        addProperty("reference", "/modules/magnolia-vanity-url/dialogs/generic/tabVanity")
    );

    private final Task _stkDialogs = new NodeBuilderTask("Dialogs", "Add Vanity URI Mapping to STK", logging, CONFIG, "/modules/standard-templating-kit",
        getNode("dialogs/pages").then(
            getNode("faq/stkFAQProperties").then(VANITY_OPS),
            getNode("form/stkFormProperties").then(VANITY_OPS),
            getNode("news/stkNewsProperties").then(VANITY_OPS),
            getNode("event/stkEventProperties").then(VANITY_OPS),
            getNode("home/stkHomeProperties").then(VANITY_OPS),
            getNode("article/stkArticleProperties").then(VANITY_OPS),
            getNode("section/stkSectionProperties").then(VANITY_OPS),
            getNode("siteMap/stkSiteMapProperties").then(VANITY_OPS),
            getNode("glossary/stkGlossaryProperties").then(VANITY_OPS),
            getNode("glossary/stkGlossaryTermProperties").then(VANITY_OPS),
            getNode("glossary/stkGlossaryLetterProperties").then(VANITY_OPS),
            getNode("largeArticle/stkLargeArticleProperties").then(VANITY_OPS),
            getNode("imageGallery/stkImageGalleryProperties").then(VANITY_OPS),
            getNode("newsOverview/stkNewsOverviewProperties").then(VANITY_OPS),
            getNode("eventsOverview/stkEventsOverviewProperties").then(VANITY_OPS),
            getNode("searchResult/stkSearchResultProperties").then(VANITY_OPS),
            getNode("categoryOverview/stkCategoryOverviewProperties").then(VANITY_OPS)
        )
    );

    private final Task _adminPageConfig = new NodeBuilderTask("Page Config", "Add Admin Central Page for Vanity Url", logging, CONFIG, "/modules/adminInterface",
        getNode("config/menu/tools").then(addNode("vanityUrl", CONTENTNODE.getSystemName()).then(
            addProperty("i18nBasename", "com.aperto.magnolia.vanity.messages"),
            addProperty("icon", "/.resources/icons/16/dot.gif"),
            addProperty("label", "menu.tools.vanity"),
            addProperty("onclick", "MgnlAdminCentral.showContent('/.magnolia/pages/vanity.html');")
        ))
    );

    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(_adminPageConfig);
        if (installContext.isModuleRegistered(STANDARD_TEMPLATING_KIT)) {
            tasks.add(_stkDialogs);
        }
        return tasks;
    }
}