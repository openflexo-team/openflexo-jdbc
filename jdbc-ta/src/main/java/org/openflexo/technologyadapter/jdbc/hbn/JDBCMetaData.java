/**
 *
 * Copyright (c) 2014-2015, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
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

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;

/**
 * Declares the FML meta-data (annotation) vocabulary used to map a contract {@link FlexoConcept} (and its {@link FlexoProperty properties})
 * onto a relational database, in a way that is symmetric with the mechanism used in <code>openflexo-xml</code> (<code>@XMLElement</code>)
 * and <code>openflexo-xlsx</code> (<code>@DataRange</code>/<code>@Property</code>).
 *
 * <p>
 * Those meta-data are ordinary FML annotations, parsed uniformly by <code>openflexo-core/fml-parser</code> into
 * {@link org.openflexo.foundation.fml.md.FMLMetaData} objects and read reflectively through {@link FlexoConcept#getMetaData(String)} /
 * {@link FlexoProperty#getMetaData(String)}. This class only gives those keys their JDBC semantics.
 * </p>
 *
 * <p>
 * Vocabulary:
 * </p>
 * <ul>
 * <li><b>Concept &rarr; table</b>: <code>@Table("TABLE_NAME")</code>. When absent, the table name defaults to the concept name.</li>
 * <li><b>Scalar property &rarr; column</b>: <code>@Property(column="COLUMN_NAME")</code>. When absent, the column defaults to the property
 * name.</li>
 * <li><b>Identifier</b>: <code>@Property(column="ID", id="true")</code> flags a property as (part of) the concept key (FML currently has no
 * textual <code>key</code> keyword).</li>
 * <li><b>To-one reference</b> (property typed as a concept): <code>@Property(column="FK_COLUMN", fk="REFERENCED_ATTRIBUTE")</code>. The
 * referenced concept is inferred from the FML type of the property.</li>
 * <li><b>One-to-many reference</b> (property typed as a concept, cardinality <code>[0,*]</code>):
 * <code>@Property(mappedBy="DESTINATION_KEY_COLUMN")</code>.</li>
 * </ul>
 *
 * @author sylvain
 */
public class JDBCMetaData {

	/** Concept-level annotation binding a {@link FlexoConcept} to a database table: <code>@Table("TABLE_NAME")</code> */
	public static final String TABLE = "Table";

	/** Property-level annotation binding a {@link FlexoProperty} to a database column or reference: <code>@Property(...)</code> */
	public static final String PROPERTY = "Property";

	/** Key of {@link #PROPERTY} multi-valued meta-data holding the database column name */
	public static final String COLUMN = "column";

	/** Key of {@link #PROPERTY} multi-valued meta-data flagging the property as (part of) the concept identifier (value <code>true</code>) */
	public static final String ID = "id";

	/** Key of {@link #PROPERTY} multi-valued meta-data holding the referenced attribute name of a to-one reference (foreign key) */
	public static final String FK = "fk";

	/** Key of {@link #PROPERTY} multi-valued meta-data holding the destination key column of a one-to-many reference */
	public static final String MAPPED_BY = "mappedBy";

	/**
	 * Return the database table name declared for supplied {@link FlexoConcept} through a <code>@Table</code> annotation, or the concept name
	 * when no such annotation is declared.
	 */
	public static String getTableName(FlexoConcept concept) {
		String tableName = concept.getSingleMetaData(TABLE, String.class);
		if (tableName != null) {
			return tableName;
		}
		return concept.getName();
	}

	/**
	 * Return the {@link MultiValuedMetaData} carried by a <code>@Property(...)</code> annotation on supplied {@link FlexoProperty}, or
	 * <code>null</code> when the property carries no such annotation.
	 */
	public static MultiValuedMetaData getPropertyMetaData(FlexoProperty<?> property) {
		if (property.getMetaData(PROPERTY) instanceof MultiValuedMetaData) {
			return (MultiValuedMetaData) property.getMetaData(PROPERTY);
		}
		return null;
	}

	/**
	 * Return the database column name declared for supplied {@link FlexoProperty} through a <code>@Property(column=...)</code> annotation, or
	 * the property name when no column is explicitly declared.
	 */
	public static String getColumnName(FlexoProperty<?> property) {
		MultiValuedMetaData md = getPropertyMetaData(property);
		if (md != null) {
			String column = md.getValue(COLUMN, String.class);
			if (column != null) {
				return column;
			}
		}
		return property.getName();
	}

	/**
	 * Return the referenced attribute name (foreign key) declared through a <code>@Property(fk=...)</code> annotation, or <code>null</code>
	 * when the property is not a to-one reference.
	 */
	public static String getForeignKeyAttribute(FlexoProperty<?> property) {
		MultiValuedMetaData md = getPropertyMetaData(property);
		return (md != null ? md.getValue(FK, String.class) : null);
	}

	/**
	 * Return the destination key column declared through a <code>@Property(mappedBy=...)</code> annotation, or <code>null</code> when the
	 * property is not a one-to-many reference.
	 */
	public static String getMappedBy(FlexoProperty<?> property) {
		MultiValuedMetaData md = getPropertyMetaData(property);
		return (md != null ? md.getValue(MAPPED_BY, String.class) : null);
	}

	/**
	 * Return <code>true</code> when supplied {@link FlexoProperty} is flagged as (part of) the concept identifier through a
	 * <code>@Property(id="true")</code> annotation.
	 */
	public static boolean isIdentifier(FlexoProperty<?> property) {
		MultiValuedMetaData md = getPropertyMetaData(property);
		return (md != null && "true".equalsIgnoreCase(md.getValue(ID, String.class)));
	}

	/**
	 * Return <code>true</code> when supplied {@link FlexoProperty} carries a <code>@Property(...)</code> annotation mapping it to a database
	 * column (as opposed to a to-one or one-to-many reference).
	 */
	public static boolean isColumn(FlexoProperty<?> property) {
		MultiValuedMetaData md = getPropertyMetaData(property);
		return (md != null && getForeignKeyAttribute(property) == null && getMappedBy(property) == null);
	}

	/**
	 * Return <code>true</code> when supplied {@link FlexoProperty} carries a to-one reference annotation (<code>@Property(fk=...)</code>).
	 */
	public static boolean isToOneReference(FlexoProperty<?> property) {
		return getForeignKeyAttribute(property) != null;
	}

	/**
	 * Return <code>true</code> when supplied {@link FlexoProperty} carries a one-to-many reference annotation
	 * (<code>@Property(mappedBy=...)</code>).
	 */
	public static boolean isToManyReference(FlexoProperty<?> property) {
		return getMappedBy(property) != null;
	}
}
