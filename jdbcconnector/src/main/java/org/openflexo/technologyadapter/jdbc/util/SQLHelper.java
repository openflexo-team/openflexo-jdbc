package org.openflexo.technologyadapter.jdbc.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQL request helper
 */
public class SQLHelper {


	// TODO complete with http://dev.mysql.com/doc/refman/5.7/en/tables-table.html
    public static final String SELECT_TABLES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA='PUBLIC'";

	public static final String SELECT_COLUMNS = "SELECT COLUMN_NAME, DTD_IDENTIFIER FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=? ORDER BY ORDINAL_POSITION";

	public static final String SELECT_PRIMARY_KEY = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME=?";

	public static ModelFactory getFactory(JDBCConnection model) {
		// Find the correct factory
		if (model.getResource() instanceof JDBCResource) {
			return ((JDBCResource) model.getResource()).getFactory();
		} else {
			try {
				return new ModelFactory(JDBCConnection.class);
			} catch (ModelDefinitionException e) {
				return null;
			}
		}
	}

    /** Requests through SQL the list of table for one connection */
    public static List<JDBCTable> getTables(
    		final JDBCSchema schema, final ModelFactory factory
	) throws SQLException {
		Connection connection = schema.getModel().getConnection();
        return new QueryRunner().query(connection, SELECT_TABLES, new ResultSetHandler<List<JDBCTable>>() {
			@Override
			public List<JDBCTable> handle(ResultSet resultSet) throws SQLException {
				ArrayList<JDBCTable> tables = new ArrayList<>();
				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");
					JDBCTable table = factory.newInstance(JDBCTable.class);
					table.init(schema, tableName);
					tables.add(table);
				}
				return tables;
			}
		});

    }

    public static List<JDBCColumn> getTableColumns(final JDBCTable table, final ModelFactory factory) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
		final Set<String> keys = getKeys(table);
		return new QueryRunner().query(connection, SELECT_COLUMNS, new ResultSetHandler<List<JDBCColumn>>() {
			@Override
			public List<JDBCColumn> handle(ResultSet resultSet) throws SQLException {
				ArrayList<JDBCColumn> columns = new ArrayList<>();
				while (resultSet.next()) {
					JDBCColumn column = factory.newInstance(JDBCColumn.class);
					String name = resultSet.getString(1);
					column.init(table, keys.contains(name), name, resultSet.getString(2));
					columns.add(column);
				}
				return columns;
			}
		}, sqlName(table.getName()));
    }

    private static Set<String> getKeys(final JDBCTable table) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
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

    public static JDBCTable createTable(final JDBCSchema schema, final ModelFactory factory, final String tableName, String[] ... attributes) throws SQLException {
		Connection connection = schema.getModel().getConnection();
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
		int length = request.length();

		for (String[] attribute : attributes) {
			if (length < request.length()) request.append(", ");

			int localLength = request.length();
			for (String part : attribute) {
				if (localLength < request.length()) request.append(" ");
				request.append(sqlName(part));
			}
		}
		request.append(")");

		return request.toString();
	}


	public static void dropTable(
    		final JDBCSchema schema, final String tableName
	) throws SQLException {
		Connection connection = schema.getModel().getConnection();
		new QueryRunner().update(connection, "DROP TABLE " + sqlName(tableName));
	}

	public static JDBCColumn createColumn(
			final JDBCTable table, final ModelFactory factory, final String columnName, final String type, boolean key
	) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
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
		Connection connection = table.getSchema().getModel().getConnection();
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

	public static JDBCResultSet select(
		JDBCConnection connection, final ModelFactory factory, final JDBCTable from, String where, String orderBy, int limit, int offset)
		throws SQLException
	{
		String request = createSelectRequest(from, where, orderBy, limit, offset);
		return new QueryRunner().query(connection.getConnection(), request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				return constructJdbcResult(factory, resultSet, from);
			}
		});
	}

	private static String createSelectRequest(final JDBCTable from, String where, String orderBy, int limit, int offset) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM ");
		result.append(from.getName());
		if (where != null) {
			result.append(" WHERE ");
			result.append(where);
		}
		if (orderBy != null) {
			result.append(" ORDER BY ");
			result.append(orderBy);
		}
		if (limit > 0) {
			result.append(" LIMIT ");
			result.append(limit);
		}
		if (offset > 0) {
			result.append(" OFFSET ");
			result.append(offset);
		}
		return result.toString();
	}

	public static JDBCResultSet insert(final JDBCLine line) throws SQLException {
		final JDBCConnection connection = line.getTable().getSchema().getModel();
		String request = createInsertRequest(line);
		return new QueryRunner().insert(connection.getConnection(), request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				ModelFactory factory = getFactory(connection);
				return constructJdbcResult(factory, resultSet, line.getTable());
			}
		});
	}

	private static String createInsertRequest(JDBCLine line) {
		StringBuilder result = new StringBuilder();
		result.append("INSERT INTO ");
		result.append(line.getTable().getName());
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

	/** Create JDBCResultSet from a ResultSet */
	private static JDBCResultSet constructJdbcResult(ModelFactory factory, ResultSet resultSet, JDBCTable from) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		// searches for columns
		int columnCount = metaData.getColumnCount();
		JDBCColumn[] columns = new JDBCColumn[columnCount];
		for (int i = 1; i <= columnCount; i++) {
			columns[i-1] = from.getColumn(metaData.getColumnName(i));
		}

		List<JDBCLine> lines = new ArrayList<>();
		while (resultSet.next()) {
			JDBCLine line = factory.newInstance(JDBCLine.class);
			List<JDBCValue> values = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				JDBCValue value = factory.newInstance(JDBCValue.class);
				value.init(line, columns[i-1], resultSet.getString(i));
				values.add(value);
			}
			line.init(from, values);
			lines.add(line);

		}

		JDBCResultSet result = factory.newInstance(JDBCResultSet.class);
		result.init(from, lines);
		return result;
	}

	public static void update(JDBCValue value, String newValue) throws SQLException {
		Connection connection = value.getLine().getTable().getSchema().getModel().getConnection();
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
		for (JDBCColumn whereColumn : line.getTable().getColumns()) {
			if (whereColumn.isPrimaryKey()) {
				if (length < result.length()) result.append(" AND ");

				result.append(whereColumn.getName());
				result.append( " = ");
				result.append(sqlValue(whereColumn.getType(), line.getValue(whereColumn).getValue()));
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
