/**
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

package org.openflexo.technologyadapter.jdbc.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

/**
 * SQL request helper
 * 
 * @author Jean-Charles Roger
 * @author xtof
 * 
 *         TODO: better manage catalog,
 * 
 *         TODO: test with connection to other databases
 * 
 */
public class SQLHelper {

	public static JDBCFactory getFactory(JDBCConnection model) {
		// Find the correct factory
		if (model.getResource() instanceof JDBCResource) {
			return ((JDBCResource) model.getResource()).getFactory();
		}
		else {
			try {
				return new JDBCFactory();
			} catch (ModelDefinitionException e) {
				return null;
			}
		}
	}

	/**
	 * Updates the list of tables for the given schema.
	 * 
	 * @param schema
	 *            the schema
	 * @param tables
	 *            the table list to update
	 * @param factory
	 *            the factory used to create the new tables if needed
	 */
	public static void updateTables(final JDBCSchema schema, List<JDBCTable> tables, final JDBCFactory factory) throws SQLException {
		Connection connection = schema.getResourceData().getConnection();

		// prepare case ignoring map to match tables
		final Map<String, JDBCTable> sortedTables = new HashMap<>();
		for (JDBCTable table : tables) {
			sortedTables.put(table.getName().toLowerCase(), table);
		}

		// query the tables to find new and removed ones
		final Set<JDBCTable> added = new LinkedHashSet<>();
		final Set<JDBCTable> matched = new LinkedHashSet<>();

		DatabaseMetaData metadata = connection.getMetaData();

		ResultSet jdbcTables = metadata.getTables(connection.getCatalog(), "PUBLIC", "%", null);
		while (jdbcTables.next()) {
			String tableName = jdbcTables.getString("TABLE_NAME");
			JDBCTable aTable = sortedTables.get(tableName.toLowerCase());
			if (aTable == null) {
				// new table, add it to the list
				aTable = factory.newInstance(JDBCTable.class);
				aTable.init(schema, tableName);
				added.add(aTable);
			}
			else {
				matched.add(aTable);
			}
		}

		// gets tables to remove
		Set<JDBCTable> removed = new HashSet<>();
		for (JDBCTable table : tables) {
			if (!matched.contains(table))
				removed.add(table);
		}

		// clears the tables of the removed ones
		// using schema adder and removed fires notifications
		for (JDBCTable table : removed) {
			schema.removeTable(table);
		}
		// adds new tables
		for (JDBCTable table : added) {
			schema.addTable(table);
		}
	}

	/**
	 * Updates the list of columns for the given table.
	 * 
	 * @param table
	 *            the table
	 * @param columns
	 *            the table list to update
	 * @param factory
	 *            the factory used to create the new columns if needed
	 */
	public static void updateColumns(final JDBCTable table, List<JDBCColumn> columns, final JDBCFactory factory) throws SQLException {
		Connection connection = table.getResourceData().getConnection();

		// retrieves keys
		final Set<String> keys = getKeys(table);

		// prepare case ignoring map to match columns
		final Map<String, JDBCColumn> sortedColumns = new HashMap<>();
		for (JDBCColumn column : columns) {
			sortedColumns.put(column.getName().toLowerCase(), column);
		}

		// query the columns to find new and removed ones
		final Set<JDBCColumn> added = new LinkedHashSet<>();
		final Set<JDBCColumn> matched = new LinkedHashSet<>();

		DatabaseMetaData metadata = connection.getMetaData();

		ResultSet jdbcCols = metadata.getColumns(connection.getCatalog(), "PUBLIC", sqlName(table.getName()), "%");
		while (jdbcCols.next()) {
			String name = jdbcCols.getString("COLUMN_NAME");
			JDBCColumn column = sortedColumns.get(name.toLowerCase());
			if (column == null) {
				// new column, add it to the list
				column = factory.newInstance(JDBCColumn.class);
				column.init(table, keys.contains(name), name, jdbcCols.getString("TYPE_NAME"));
				added.add(column);
			}
			else {
				matched.add(column);
			}
		}

		// gets columns to remove
		Set<JDBCColumn> removed = new HashSet<>();
		for (JDBCColumn column : columns) {
			if (!matched.contains(column))
				removed.add(column);
		}

		// clears the columns of the removed ones
		// using table adder and removed fires notifications
		for (JDBCColumn column : removed) {
			table.removeColumn(column);
		}
		// adds new columns
		for (JDBCColumn column : added) {
			table.addColumn(column);
		}
	}

	private static Set<String> getKeys(final JDBCTable table) throws SQLException {
		Connection connection = table.getResourceData().getConnection();

		DatabaseMetaData metadata = connection.getMetaData();

		ResultSet foundKeys = metadata.getPrimaryKeys(connection.getCatalog(), "PUBLIC", sqlName(table.getName()));

		Set<String> keys = new HashSet<>();
		while (foundKeys.next()) {
			keys.add(foundKeys.getString("COLUMN_NAME"));
		}
		return keys;

	}

	public static JDBCTable createTable(final JDBCSchema schema, final JDBCFactory factory, final String tableName, String[]... attributes)
			throws SQLException {
		Connection connection = schema.getResourceData().getConnection();
		String request = createTableRequest(tableName, attributes);
		return new QueryRunner().insert(connection, request, resultSet -> {
			JDBCTable table = factory.newInstance(JDBCTable.class);
			table.init(schema, tableName);
			return table;
		});
	}

	private static String createTableRequest(String name, String[]... attributes) throws SQLException {
		StringBuilder request = new StringBuilder("CREATE TABLE ");
		request.append(sqlName(name));
		request.append(" (");

		if (attributes != null) {
			int length = request.length();
			for (String[] attribute : attributes) {
				if (length < request.length())
					request.append(", ");

				int localLength = request.length();
				for (String part : attribute) {
					if (localLength < request.length())
						request.append(" ");
					request.append(sqlName(part));
				}
			}
		}
		request.append(")");

		return request.toString();
	}

	public static void dropTable(final JDBCSchema schema, final String tableName) throws SQLException {
		Connection connection = schema.getResourceData().getConnection();
		new QueryRunner().update(connection, "DROP TABLE " + sqlName(tableName));
	}

	public static JDBCColumn createColumn(final JDBCTable table, final JDBCFactory factory, final String columnName, final String type,
			boolean key) throws SQLException {
		Connection connection = table.getResourceData().getConnection();
		String addColumn = createAddColumnRequest(table, columnName, type, key);
		new QueryRunner().update(connection, addColumn);

		JDBCColumn column = factory.newInstance(JDBCColumn.class);
		column.init(table, key, columnName, type);
		return column;
	}

	private static String createAddColumnRequest(JDBCTable table, String columnName, String type, boolean key) {
		StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(sqlName(table.getName()));
		result.append(" ADD ");
		result.append(sqlName(columnName));
		result.append(" ");
		result.append(type);
		if (key) {
			result.append(" PRIMARY KEY");
		}
		return result.toString();
	}

	public static void dropColumn(final JDBCTable table, final String columnName) throws SQLException {
		Connection connection = table.getResourceData().getConnection();
		String dropColumn = "ALTER TABLE " + sqlName(table.getName()) + " DROP COLUMN " + sqlName(columnName);
		new QueryRunner().update(connection, dropColumn);
	}

	public static void grant(JDBCConnection connection, String access, String on, String user) throws SQLException {
		String grant = "GRANT " + access + " ON " + sqlName(on) + " TO " + sqlName(user) + "";
		new QueryRunner().update(connection.getConnection(), grant);
	}

	public static String sqlName(String name) {
		return name.toUpperCase();
	}

	public enum JoinType {
		NoJoin, InnerJoin, CrossJoin, LeftJoin, RightJoin, FullJoin, SelfJoin, NaturalJoin, UnionJoin;

		@Override
		public String toString() {
			switch (this) {
				case InnerJoin:
					return "INNER JOIN";
				case CrossJoin:
					return "CROSS JOIN";
				case LeftJoin:
					return "LEFT JOIN";
				case RightJoin:
					return "RIGHT JOIN";
				case FullJoin:
					return "FULL JOIN";
				case SelfJoin:
					return "SELF JOIN";
				case NaturalJoin:
					return "NATURAL JOIN";
				case UnionJoin:
					return "UNION JOIN";
				default:
					return "";
			}
		}
	}

	public static JDBCResultSet select(final JDBCFactory factory, final JDBCTable from, String where, String orderBy, int limit, int offset)
			throws SQLException {
		Connection connection = from.getResourceData().getConnection();
		final JDBCResultSetDescription description = factory.makeResultSetDescription(from.getResourceData(), from.getName(), null, null,
				null, where, orderBy, limit, offset);
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection, request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				return factory.makeJDBCResult(description, resultSet, from.getSchema());
			}
		});
	}

	public static JDBCResultSet select(final JDBCFactory factory, final JDBCTable from, String joinType, JDBCTable join, String on,
			String where, String orderBy, int limit, int offset) throws SQLException {
		Connection connection = from.getResourceData().getConnection();
		final JDBCResultSetDescription description = factory.makeResultSetDescription(from.getResourceData(), from.getName(), joinType,
				join.getName(), on, where, orderBy, limit, offset);
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection, request, resultSet -> factory.makeJDBCResult(description, resultSet, from.getSchema()));
	}

	public static JDBCResultSet select(final JDBCFactory factory, final JDBCConnection connection,
			final JDBCResultSetDescription description) throws SQLException {
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection.getConnection(), request,
				resultSet -> factory.makeJDBCResult(description, resultSet, connection.getSchema()));
	}

	private static String createSelectRequest(JDBCResultSetDescription description) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM ");
		result.append(description.getFrom());
		JoinType joinType = description.decodeJoinType();
		if (joinType != JoinType.NoJoin && description.getJoin() != null) {
			result.append(" ");
			result.append(joinType);

			result.append(" ");
			result.append(description.getJoin());

			if (description.getOn() != null) {
				result.append(" ON ");
				result.append(description.getOn());
			}
		}

		if (description.getWhere() != null) {
			result.append(" WHERE ");
			result.append(description.getWhere());
		}
		if (description.getOrderBy() != null) {
			result.append(" ORDER BY ");
			result.append(description.getOrderBy());
		}
		if (description.getLimit() > 0) {
			result.append(" LIMIT ");
			result.append(description.getLimit());
		}
		if (description.getOffset() > 0) {
			result.append(" OFFSET ");
			result.append(description.getOffset());
		}
		return result.toString();
	}

	public static JDBCLine insert(final JDBCLine line, final JDBCTable table) throws SQLException {
		final JDBCConnection connection = table.getResourceData();
		String request = createInsertRequest(line, table);
		String resultKey = new QueryRunner().insert(connection.getConnection(), request, resultSet -> {
			if (resultSet.getMetaData().getColumnCount() > 0) {
				resultSet.next();
				return resultSet.getString(1);
			}
			return null;
		});

		String primaryKey = resultKey != null ? resultKey : line.getKeys().get(0);
		return table.find(primaryKey);
	}

	private static String createInsertRequest(JDBCLine line, JDBCTable table) {
		StringBuilder result = new StringBuilder();
		result.append("INSERT INTO ");
		result.append(table.getName());
		result.append(" (");
		int length = result.length();
		for (JDBCValue value : line.getValues()) {
			if (length < result.length())
				result.append(",");
			result.append(value.getColumn().getName());
		}
		result.append(") VALUES (");
		length = result.length();
		for (JDBCValue value : line.getValues()) {
			if (length < result.length())
				result.append(",");
			result.append(sqlValue(value.getColumn().getTypeAsString(), value.getValue()));
		}
		result.append(")");
		return result.toString();
	}

	public static void update(JDBCValue value, String newValue) throws SQLException {
		Connection connection = value.getResourceData().getConnection();
		String request = createUpdateRequest(value, newValue);
		new QueryRunner().update(connection, request);
	}

	private static String createUpdateRequest(JDBCValue value, String newValue) {
		StringBuilder result = new StringBuilder();
		JDBCColumn column = value.getColumn();
		result.append("UPDATE ");
		result.append(column.getTable().getName());
		result.append(" SET ");
		result.append(column.getName());
		result.append(" = ");

		result.append(sqlValue(value.getColumn().getTypeAsString(), newValue));

		result.append(" WHERE ");
		int length = result.length();
		JDBCLine line = value.getLine();
		for (JDBCValue otherValue : line.getValues()) {
			JDBCColumn whereColumn = otherValue.getColumn();
			if (whereColumn.isPrimaryKey()) {
				if (length < result.length())
					result.append(" AND ");

				result.append(whereColumn.getName());
				result.append(" = ");
				result.append(sqlValue(whereColumn.getTypeAsString(), otherValue.getValue()));
			}
		}
		return result.toString();
	}

	public static boolean delete(final JDBCLine line, final JDBCTable table) throws SQLException {
		final JDBCConnection connection = table.getResourceData();
		String request = createDeleteRequest(line, table);
		int primaryKey = new QueryRunner().update(connection.getConnection(), request);
		return primaryKey != 1;
	}

	private static String createDeleteRequest(JDBCLine line, JDBCTable table) {
		StringBuilder result = new StringBuilder();
		result.append("DELETE FROM ");
		result.append(table.getName());
		result.append(" WHERE ");
		int length = result.length();
		for (JDBCValue value : line.getValues()) {
			if (value.getColumn().isPrimaryKey()) {
				if (length < result.length())
					result.append(" and");
				result.append(value.getColumn().getName());
				result.append(" = ");
				result.append(sqlValue(value.getColumn().getTypeAsString(), value.getValue()));
			}
		}
		return result.toString();
	}

	public static String sqlValue(String type, String value) {
		StringBuilder result = new StringBuilder();
		boolean needsQuotes = needsQuotes(type);
		if (needsQuotes)
			result.append("'");
		result.append(value);
		if (needsQuotes)
			result.append("'");
		return result.toString();
	}

	public static boolean needsQuotes(String type) {
		String upperCaseType = type.toUpperCase();
		return upperCaseType.startsWith("CHAR") || upperCaseType.startsWith("VARCHAR") || upperCaseType.startsWith("CLOB");
	}

}
