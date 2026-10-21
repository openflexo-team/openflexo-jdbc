/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.jdbc.hbn;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;

/**
 * Utilities for the Hibernate metadata that this adapter builds programmatically (without annotation or hbm.xml scanning)
 */
public class HbnMetadataUtils {

	private HbnMetadataUtils() {
	}

	/**
	 * Completes a mapping that was declared programmatically. Hibernate 6 expects work that the annotation binder does while it builds the
	 * metadata, and that a programmatic mapping never goes through:
	 * <ul>
	 * <li>the type of each {@link BasicValue} is resolved</li>
	 * <li>a to-one association that references the identifier of its target references the primary key (it cannot name the identifier
	 * property)</li>
	 * <li>each column of a table is bound to a value (the table exporter and the column ordering read it)</li>
	 * <li>a property whose columns are nullable is optional</li>
	 * </ul>
	 * Call it before validating the metadata or building a session factory.
	 * 
	 * @param metadata
	 *            the metadata to complete
	 * @param context
	 *            the context that was used to build the mapping
	 */
	public static void complete(Metadata metadata, MetadataBuildingContext context) {
		for (PersistentClass persistentClass : metadata.getEntityBindings()) {
			resolve(persistentClass.getIdentifier());
			for (Property property : persistentClass.getProperties()) {
				resolve(property.getValue());
				referencePrimaryKey(metadata, property.getValue());
				// Hibernate 5 considered a property optional when its columns are nullable, Hibernate 6 only reads the flag
				if (!property.isOptional() && property.getValue().isNullable()) {
					property.setOptional(true);
				}
			}
			if (persistentClass.getIdentifierProperty() != null) {
				resolve(persistentClass.getIdentifierProperty().getValue());
			}
		}
		for (Table table : metadata.collectTableMappings()) {
			for (Column column : table.getColumns()) {
				if (column.getValue() == null) {
					BasicValue value = new BasicValue(context, table);
					value.setTypeName("java.lang.String");
					value.addColumn(column);
					value.resolve();
				}
			}
		}
	}

	private static void resolve(Value value) {
		if (value instanceof BasicValue && ((BasicValue) value).getResolution() == null) {
			((BasicValue) value).resolve();
		}
	}

	private static void referencePrimaryKey(Metadata metadata, Value value) {
		if (value instanceof ManyToOne) {
			ManyToOne manyToOne = (ManyToOne) value;
			String referenced = manyToOne.getReferencedPropertyName();
			PersistentClass target = manyToOne.getReferencedEntityName() != null
					? metadata.getEntityBinding(manyToOne.getReferencedEntityName()) : null;
			if (referenced != null && target != null && target.getIdentifierProperty() != null
					&& referenced.equalsIgnoreCase(target.getIdentifierProperty().getName())) {
				manyToOne.setReferencedPropertyName(null);
				manyToOne.setReferenceToPrimaryKey(true);
			}
		}
	}
}
