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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

/**
 * A value inside a JDBCLine
 */
@ModelEntity
@ImplementationClass(JDBCValue.JDBCValueImpl.class)
public interface JDBCValue extends FlexoObject, InnerResourceData<JDBCConnection> {

	String LINE = "line";
	String COLUMN = "column";
	String VALUE = "value";

	// TODO add **readonly** attribute

	@Initializer
	void init(@Parameter(LINE) JDBCLine line, @Parameter(COLUMN) JDBCColumn column, @Parameter(VALUE) String value);

	@Getter(LINE)
	JDBCLine getLine();

	@Getter(COLUMN)
	JDBCColumn getColumn();

	@Getter(VALUE)
	String getValue();

	int getIntValue();

	void setIntValue(int newValue);

	@Setter(VALUE)
	boolean setValue(String value);

	abstract class JDBCValueImpl extends FlexoObjectImpl implements JDBCValue {
		private static final Logger LOGGER = Logger.getLogger(JDBCTable.class.getPackage().getName());

		@Override
		public boolean setValue(String value) {
			try {
				SQLHelper.update(this, value);
				performSuperSetter(VALUE, value);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't update value on column '"+ getColumn().getTable().getName() + "." + getColumn().getName()+"'", e);
				return false;
			}
		}

		@Override
		public int getIntValue() {
			String value = getValue();
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		@Override
		public void setIntValue(int newValue) {
			setValue(Integer.toString(newValue));
		}

		@Override
		public JDBCConnection getResourceData() {
			return getLine().getResourceData();
		}

		@Override
		public String toString() {
			StringBuilder columnDescription = new StringBuilder();
			JDBCColumn column = getColumn();
			if (column != null) {
				JDBCTable table = column.getTable();
				if (table != null) {
					columnDescription.append(table.getName());
					columnDescription.append(".");
				}
				columnDescription.append(column.getName());
			} else {
				columnDescription.append("none");
			}
			return "[" + columnDescription + "] " + getValue();
		}
	}
}
