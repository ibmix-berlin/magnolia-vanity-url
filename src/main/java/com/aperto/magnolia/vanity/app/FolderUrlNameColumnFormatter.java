package com.aperto.magnolia.vanity.app;


import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Table;

public class FolderUrlNameColumnFormatter extends	AbstractColumnFormatter<PropertyColumnDefinition> {
	
	private static final Logger log = LoggerFactory.getLogger(FolderUrlNameColumnFormatter.class);
	private static final String ICON_TAG = "<span class=\"%s v-table-icon-element\"></span>";

	public FolderUrlNameColumnFormatter(PropertyColumnDefinition definition) {
		super(definition);
	}

	public Object generateCell(Table source, Object itemId, Object columnId) {
		Item jcrItem = getJcrItem(source, itemId);
		if ((jcrItem != null) && (jcrItem.isNode())) {
			Node node = (Node) jcrItem;			
			try {				
				if (node.getPrimaryNodeType().getName().equals("mgnl:vanityUrl")) {
					return node.getProperty("vanityUrl").getString();
				} else {
					return node.getName();
				}
			} catch (RepositoryException e) {
				log.warn(
						"Unable to get the displayed value for the name column ",
						e);
			}
		}
		return "";
	}

}