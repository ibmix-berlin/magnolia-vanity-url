package com.aperto.magnolia.vanity.setup;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import org.apache.jackrabbit.value.StringValue;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.List;

import static com.aperto.magnolia.vanity.VanityUrlPage.PN_VANITY;
import static com.aperto.magnolia.vanity.VanityUrlPage.queryVanityTargetNodes;
import static info.magnolia.repository.RepositoryConstants.WEBSITE;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Task to migrate all existing vanity urls to multi value properties.
 *
 * @author frank.sommer
 * @since 30.01.14
 */
public class MigrateVanityPropertiesTask extends AbstractTask {

    public MigrateVanityPropertiesTask() {
        super("Migrate vanity url", "Migrate existing vanity url settings.");
    }

    @Override
    public void execute(final InstallContext installContext) throws TaskExecutionException {
        try {
            List<Node> nodes = queryVanityTargetNodes();
            for (Node node : nodes) {
                Property property = node.getProperty(PN_VANITY);
                if (!property.isMultiple()) {
                    String propValue = property.getString();
                    if (isNotBlank(propValue)) {
                        property.remove();
                        Value[] values = new Value[1];
                        values[0] = new StringValue(propValue);
                        node.setProperty(PN_VANITY, values);
                    }
                }
            }
            MgnlContext.getJCRSession(WEBSITE).save();
        } catch (RepositoryException e) {
            throw new TaskExecutionException("Error retrieving vanity target nodes.", e);
        }
    }
}
