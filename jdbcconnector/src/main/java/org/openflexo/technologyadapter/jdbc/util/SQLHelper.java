package org.openflexo.technologyadapter.jdbc.util;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL request helper
 */
public class SQLHelper {


	// TODO complete with http://dev.mysql.com/doc/refman/5.7/en/tables-table.html
    public static final String SELECT_TABLES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA='PUBLIC'";

	public static final String SELECT_COLUMNS = "SELECT COLUMN_NAME, DTD_IDENTIFIER FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=? ORDER BY ORDINAL_POSITION";

	public static final String SELECT_PRIMARY_KEY = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME=?";

	public static JDBCFactory getFactory(JDBCConnection model) {
		// Find the correct factory
		if (model.getResource() instanceof JDBCResource) {
			return ((JDBCResource) model.getResource()).getFactory();
		} else {
			try {
				return new JDBCFactory();
			} catch (ModelDefinitionException e) {
				return null;
			}
		}
	}

	/**
	 * Updates the list of tables for the given schema.
	 * @param schema the schema
	 * @param tables the table list to update
	 * @param factory the factory used to create the new tables if needed
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
		new QueryRunner().query(connection, SELECT_TABLES, new ResultSetHandler<Object>() {
			@Override
			public Object handle(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");
					JDBCTable table = sortedTables.get(tableName.toLowerCase());
					if (table == null) {
						// new table, add it to the list
						table = factory.newInstance(JDBCTable.class);
						table.init(schema, tableName);
						added.add(table);
					} else {
						matched.add(table);
					}
				}
				return null;
			}
		});

		// gets tables to remove
		Set<JDBCTable> removed = new HashSet<>();
		for (JDBCTable table : tables) {
			if (!matched.contains(table)) removed.add(table);
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
	 * @param table the table
	 * @param columns the table list to update
	 * @param factory the factory used to create the new columns if needed
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
		new QueryRunner().query(connection, SELECT_COLUMNS, new ResultSetHandler<Object>() {
			@Override
			public Object handle(ResultSet resultSet) throws SQLException {
				ArrayList<JDBCColumn> columns = new ArrayList<>();
				while (resultSet.next()) {
					String name = resultSet.getString(1);

					JDBCColumn column = sortedColumns.get(name.toLowerCase());
					if (column == null) {
						// new column, add it to the list
						column = factory.newInstance(JDBCColumn.class);
						column.init(table, keys.contains(name), name, resultSet.getString(2));
						added.add(column);
					} else {
						matched.add(column);
					}
				}
				return null;
			}
		}, sqlName(table.getName()));

		// gets columns to remove
		Set<JDBCColumn> removed = new HashSet<>();
		for (JDBCColumn column : columns) {
			if (!matched.contains(column)) removed.add(column);
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
		return new QueryRunner().query(connection, SELECT_PRIMARY_KEY, new ResultSetHandler<Set<String>>() {
			@Override
			public Set<String> handle(ResultSet resultSet) throws SQLException {
				Set<String> keys = new HashSet<>();
				while (resultSet.next()) {
					keys.add(resultSet.getString(1));
				}
				return keys;
			}
		}, sqlName(table.getName()));
	}

    public static JDBCTable createTable(final JDBCSchema schema, final JDBCFactory factory, final String tableName, String[] ... attributes) throws SQLException {
		Connection connection = schema.getResourceData().getConnection();
		String request = createTableRequest(tableName, attributes);
		return new QueryRunner().insert(connection, request, new ResultSetHandler<JDBCTable>() {
			@Override
			public JDBCTable handle(ResultSet resultSet) throws SQLException {
				JDBCTable table = factory.newInstance(JDBCTable.class);
				table.init(schema, tableName);
				return table;
			}
		});
	}

	private static String createTableRequest(String name, String[] ... attributes) throws SQLException {
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


	public static void dropTable(
    		final JDBCSchema schema, final String tableName
	) throws SQLException {
		Connection connection = schema.getResourceData().getConnection();
		new QueryRunner().update(connection, "DROP TABLE " + sqlName(tableName));
	}

	public static JDBCColumn createColumn(
			final JDBCTable table, final JDBCFactory factory, final String columnName, final String type, boolean key
	) throws SQLException {
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
		String dropColumn = "ALTER TABLE "+ sqlName(table.getName()) +" DROP COLUMN " + sqlName(columnName);
		new QueryRunner().update(connection, dropColumn);
	}

	public static void grant(JDBCConnection connection, String access, String on, String user) throws SQLException {
		String grant = "GRANT "+ access +" ON " + sqlName(on) + " TO " + sqlName(user) + "";
		new QueryRunner().update(connection.getConnection(),grant);
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

	public static JDBCResultSet select(
		final JDBCFactory factory, final JDBCTable from,
		String where, String orderBy, int limit, int offset
	)
		throws SQLException
	{
		Connection connection = from.getResourceData().getConnection();
		final JDBCResultSetDescription description = factory.makeResultSetDescription(
				from.getResourceData(), from.getName(), null, null, null, where, orderBy, limit, offset
		);
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection, request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				return factory.makeJDBCResult(description, resultSet, from.getSchema());
			}
		});
	}

	public static JDBCResultSet select(
		final JDBCFactory factory, final JDBCTable from,
		String joinType, JDBCTable join, String on,
		String where, String orderBy, int limit, int offset
	)
		throws SQLException
	{
		Connection connection = from.getResourceData().getConnection();
		final JDBCResultSetDescription description = factory.makeResultSetDescription(
				from.getResourceData(), from.getName(), joinType, join.getName(), on, where, orderBy, limit, offset
		);
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection, request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				return factory.makeJDBCResult(description, resultSet, from.getSchema());
			}
		});
	}

	public static JDBCResultSet select(final JDBCFactory factory, final JDBCConnection connection, final JDBCResultSetDescription description) throws SQLException {
		String request = createSelectRequest(description);
		return new QueryRunner().query(connection.getConnection(), request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				return factory.makeJDBCResult(description, resultSet, connection.getSchema());
			}
		});
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

	public static JDBCResultSet insert(final JDBCLine line, final JDBCTable table) throws SQLException {
		final JDBCConnection connection = table.getResourceData();
		String request = createInsertRequest(line, table);
		return new QueryRunner().insert(connection.getConnection(), request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				JDBCFactory factory = getFactory(connection);
				// TODO check for request definition
				return factory.makeJDBCResult(null, resultSet, table.getSchema());
			}
		});
	}

	private static String createInsertRequest(JDBCLine line, JDBCTable table) {
		StringBuilder result = new StringBuilder();
		result.append("INSERT INTO ");
		result.append(table.getName());
		result.append(" (");
		int length = result.length();
		for (JDBCValue value : line.getValues()) {
			if (length < result.length()) result.append(",");
			result.append(value.getColumn().getName());
		}
		result.append(") VAlUES (");
		length = result.length();
		for (JDBCValue value : line.getValues()) {
			if (length < result.length()) result.append(",");
			result.append(sqlValue(value.getColumn().getType(), value.getValue()));
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

		result.append(sqlValue(value.getColumn().getType(), newValue));

		result.append(" WHERE ");
		int length = result.length();
		JDBCLine line = value.getLine();
		for (JDBCValue otherValue: line.getValues()) {
			JDBCColumn whereColumn = otherValue.getColumn();
			if (whereColumn.isPrimaryKey()) {
				if (length < result.length()) result.append(" AND ");

				result.append(whereColumn.getName());
				result.append( " = ");
				result.append(sqlValue(whereColumn.getType(), otherValue.getValue()));
			}
		}
		return result.toString();
	}

	public static String sqlValue(String type, String value) {
		StringBuilder result = new StringBuilder();
		boolean needsQuotes = needsQuotes(type);
		if (needsQuotes) result.append("'");
		result.append(value);
		if (needsQuotes) result.append("'");
		return result.toString();
	}

	public static boolean needsQuotes(String type) {
		type = type.toUpperCase();
		return type.startsWith("CHAR") || type.startsWith("VARCHAR") || type.startsWith("CLOB");
	}

}
