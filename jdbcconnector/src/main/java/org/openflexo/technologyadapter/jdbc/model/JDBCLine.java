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

import java.util.ArrayList;
import java.util.List;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * One JDBC line
 */
@ModelEntity
@ImplementationClass(JDBCLine.JDBCLineImpl.class)
public interface JDBCLine extends FlexoObject, InnerResourceData<JDBCConnection> {

	void init(JDBCResultSet resultSet, List<JDBCValue> values);

	JDBCResultSet getResultSet();

	List<JDBCValue> getValues();

	List<String> getKeys();

	JDBCValue getValue(String columnName);

	JDBCValue getValue(String tableName, String columnName);

	JDBCValue getValue(JDBCColumn column);

	abstract class JDBCLineImpl extends FlexoObjectImpl implements JDBCLine {

		private JDBCResultSet table;
		private List<JDBCValue> values;

		@Override
		public void init(JDBCResultSet table, List<JDBCValue> values) {
			this.table = table;
			this.values = values;
		}

		@Override
		public JDBCResultSet getResultSet() {
			return table;
		}

		public List<String> getKeys() {
			List<String> keys = new ArrayList<>();
			for (JDBCValue value : values) {
				if (value.getColumn().isPrimaryKey()) {
					keys.add(value.getValue());
				}
			}
			return keys;
		}

		@Override
		public List<JDBCValue> getValues() {
			return values;
		}

		@Override
		public JDBCValue getValue(String columnName) {
			String tableName = getResultSet().getResultSetDescription().getFrom();
			return getValue(tableName, columnName);
		}

		@Override
		public JDBCValue getValue(String tableName, String columnName) {
			JDBCTable table = getResourceData().getSchema().getTable(tableName);
			if (table == null) {
				return null;
			}
			JDBCColumn column = table.getColumn(columnName);
			if (column == null) {
				return null;
			}
			return getValue(column);
		}

		@Override
		public JDBCValue getValue(JDBCColumn column) {
			for (JDBCValue value : values) {
				if (value.getColumn().equals(column)) return value;
			}
			return null;
		}

		@Override
		public JDBCConnection getResourceData() {
			return getResultSet().getResourceData();
		}

		@Override
		public String toString() {
			StringBuilder string = new StringBuilder();
			for (JDBCValue value : getValues()) {
				if (string.length() > 0) string.append(", ");
				string.append(value);
			}
			return string.toString();
		}
	}

}
