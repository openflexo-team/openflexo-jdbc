package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

import java.util.ArrayList;
import java.util.List;

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
