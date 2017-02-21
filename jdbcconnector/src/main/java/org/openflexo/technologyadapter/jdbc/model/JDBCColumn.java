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

package org.openflexo.technologyadapter.jdbc.model;

import java.util.Objects;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;

/**
 * JDBC connector column description
 */
@ModelEntity
@ImplementationClass(JDBCColumn.JDBCColumnImpl.class)
public interface JDBCColumn extends FlexoObject, InnerResourceData<JDBCConnection> {

	String TABLE = "table";
	String PRIMARY_KEY = "primaryKey";
	String NAME = "name";
	String TYPE = "type";

	@Initializer void init(
		@Parameter(TABLE) JDBCTable table, @Parameter(PRIMARY_KEY) boolean primaryKey,
		@Parameter(NAME) String name, @Parameter(TYPE) String type
	);

	@Getter(TABLE) JDBCTable getTable();

	@Getter(value = PRIMARY_KEY, defaultValue = "false")
	boolean isPrimaryKey();

	@Getter(NAME) String getName();

	@Getter(TYPE) String getType();

	abstract class JDBCColumnImpl extends FlexoObjectImpl implements JDBCColumn {

		@Override
		public JDBCConnection getResourceData() {
			return getTable().getSchema().getModel();
		}

		public boolean equals(Object other) {
			if (other instanceof JDBCColumn) {
				JDBCColumn two = (JDBCColumn) other;
				if (!Objects.equals(getName(), two.getName())) return false;
				if (getTable() == null) return two.getTable() == null;
				return Objects.equals(getTable().getName(), two.getTable().getName());
			}
			return false;
		}
	}
}
