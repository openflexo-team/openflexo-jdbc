/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCColumnActorReference.JDBCColumnActorReferenceImpl.class)
public interface JDBCColumnActorReference extends JDBCActorReference<JDBCColumn> {

	String TABLE_ID = "tableId";
	String COLUMN_ID = "columnId";

	@Getter(TABLE_ID) @XMLAttribute
	String getTableId();

	@Setter(TABLE_ID)
	void setTableId(String newId);

	@Getter(COLUMN_ID) @XMLAttribute
	String getColumnId();

	@Setter(COLUMN_ID)
	void setColumnId(String newId);

	abstract class JDBCColumnActorReferenceImpl extends JDBCActorReferenceImpl<JDBCColumn> implements JDBCColumnActorReference {

		private JDBCColumn column;

		@Override
		public String getTableId() {
			if (column != null) return column.getTable().getName();
			return (String) performSuperGetter(TABLE_ID);
		}

		@Override
		public String getColumnId() {
			if (column != null) return column.getName();
			return (String) performSuperGetter(COLUMN_ID);
		}

		@Override
		public JDBCColumn getModellingElement() {
			if (column == null) {
				JDBCConnection connection = getConnection();
				if (connection != null) {
					String tableId = getTableId();
					JDBCTable table = connection.getSchema().getTable(tableId);
					setModellingElement(table.getColumn(getColumnId()));
				}
			}
			return column;
		}

		@Override
		public void setModellingElement(JDBCColumn object) {
			if (column != object) {
				Object oldValue = column;
				column = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
