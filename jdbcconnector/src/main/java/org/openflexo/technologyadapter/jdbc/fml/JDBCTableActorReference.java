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

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCTableActorReference.JDBCTableActorReferenceImpl.class)
public interface JDBCTableActorReference extends JDBCActorReference<JDBCTable> {

	String TABLE_ID = "tableId";

	@Getter(TABLE_ID)
	@XMLAttribute
	String getTableId();

	@Setter(TABLE_ID)
	void setTableId(String newId);

	abstract class JDBCTableActorReferenceImpl extends JDBCActorReferenceImpl<JDBCTable> implements JDBCTableActorReference {

		private JDBCTable table;

		@Override
		public String getTableId() {
			if (table != null)
				return table.getName();
			return (String) performSuperGetter(TABLE_ID);
		}

		@Override
		public JDBCTable getModellingElement(boolean forceLoading) {
			if (table == null) {
				String tableId = getTableId();
				table = getConnection().getSchema().getTable(tableId);
			}
			return table;
		}

		@Override
		public void setModellingElement(JDBCTable object) {
			if (table != object) {
				Object oldValue = table;
				table = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
