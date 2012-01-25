package com.aperto.magkit.vanity.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.ItemType;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.*;
import info.magnolia.nodebuilder.NodeOperation;
import info.magnolia.nodebuilder.task.NodeBuilderTask;
import info.magnolia.voting.voters.URIStartsWithVoter;

import java.util.ArrayList;
import java.util.List;

import static info.magnolia.cms.beans.config.ContentRepository.CONFIG;
import static info.magnolia.cms.core.ItemType.CONTENTNODE;
import static info.magnolia.nodebuilder.Ops.addNode;
import static info.magnolia.nodebuilder.Ops.addProperty;
import static info.magnolia.nodebuilder.Ops.getNode;
import static info.magnolia.nodebuilder.task.ErrorHandling.logging;

/**
 * This class is optional and lets you manager the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 */
public class VanityUrlModuleVersionHandler extends DefaultModuleVersionHandler {

    public static final String STANDARD_TEMPLATING_KIT = "standard-templating-kit";
    public static final String WORKFLOW = "workflow";
    private static final String PN_REFERENCE = "reference";


    private static final NodeOperation VANITY_OPS = addNode("vanityUrl", CONTENTNODE.getSystemName()).then(
        addProperty(PN_REFERENCE, "/modules/magnolia-vanity-url/dialogs/generic/tabVanity")
    );

    private static final NodeOperation VANITY_ADMIN_OPS = addNode("vanityUrl", CONTENTNODE.getSystemName()).then(
      addProperty("icon", "/.resources/icons/16/dot.gif"),
      addProperty("label", "menu.tools.vanity"),
      addProperty("onclick", "MgnlAdminCentral.showContent('/.magnolia/pages/VanityURIPage.html');")
    );


        private Task createBundlesNode = new CreateNodeTask(
        "Create messages bundles node",
        "Adds messages bundles node to the adminInterface configuration.",
        ContentRepository.CONFIG,
        "/modules/adminInterface/pages/messages",
        "bundles",
        ItemType.CONTENTNODE.getSystemName());

    private Task registerMessagesBundle = new NewPropertyTask(
        "Bundle",
        "Registers module messages bundle.",
        ContentRepository.CONFIG,
        "/modules/adminInterface/pages/messages/bundles",
        "vanity",
        "com.aperto.magkit.vanity.messages");

    private final Task _cacheConfig = new NodeBuilderTask("Cache config", "Add Vanity URI Mapping to STK", logging, CONFIG, "/modules/standard-templating-kit",
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
        getNode("config/menu/tools").then(VANITY_ADMIN_OPS)
     );

        protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<Task>();
        tasks.add(new ModuleDependencyBootstrapTask(STANDARD_TEMPLATING_KIT));
        tasks.add(new ModuleDependencyBootstrapTask(WORKFLOW));
        tasks.add(new NodeExistsDelegateTask(
            "Register module messages bundle",
            "Registers module messages bundle. In case the '/modules/adminInterface/pages/messages/bundles' node does not exist, it first creates it.",
            ContentRepository.CONFIG,
            "/modules/adminInterface/pages/messages/bundles",
            registerMessagesBundle,
            new ArrayDelegateTask("", createBundlesNode, registerMessagesBundle)));

        if (installContext.isModuleRegistered(STANDARD_TEMPLATING_KIT)) {
            tasks.add(_cacheConfig);
            tasks.add(_adminPageConfig);
        }
        return tasks;
    }

}