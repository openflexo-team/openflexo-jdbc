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
import java.util.List;

/**
 * SQL request helper
 */
public class SQLHelper {


	// TODO complete with http://dev.mysql.com/doc/refman/5.7/en/tables-table.html
    public static final String SELECT_TABLES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA='PUBLIC'";
    public static final String SELECT_COLUMNS = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?";

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
		return new QueryRunner().query(connection, SELECT_COLUMNS, new ResultSetHandler<List<JDBCColumn>>() {
			@Override
			public List<JDBCColumn> handle(ResultSet resultSet) throws SQLException {
				ArrayList<JDBCColumn> columns = new ArrayList<>();
				while (resultSet.next()) {
					JDBCColumn column = factory.newInstance(JDBCColumn.class);
					column.init(table, resultSet.getString(1), resultSet.getString(2));
					columns.add(column);
				}
				return columns;
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
			final JDBCTable table, final ModelFactory factory, final String columnName, final String type
	) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
		String addColumn = "ALTER TABLE "+ sqlName(table.getName()) +" ADD "+ sqlName(columnName) + " " + type;
		new QueryRunner().update(connection, addColumn);

		JDBCColumn column = factory.newInstance(JDBCColumn.class);
		column.init(table, columnName, type);
		return column;
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

	public static JDBCResultSet select(JDBCConnection connection, final ModelFactory factory, final JDBCTable from, String where, String orderBy, int limit, int offset)
			throws SQLException {
		String request = createSelectRequest(from, where, orderBy, limit, offset);
		return new QueryRunner().query(connection.getConnection(), request, new ResultSetHandler<JDBCResultSet>() {
			@Override
			public JDBCResultSet handle(ResultSet resultSet) throws SQLException {
				ResultSetMetaData metaData = resultSet.getMetaData();
				// searches for columns
				int columnCount = metaData.getColumnCount();
				JDBCColumn[] columns = new JDBCColumn[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					columns[i-1] = from.getColumn(metaData.getColumnName(i));
				}

				List<JDBCLine> lines = new ArrayList<>();
				while (resultSet.next()) {
					List<JDBCValue> values = new ArrayList<JDBCValue>();
					for (int i = 1; i <= columnCount; i++) {
						JDBCValue value = factory.newInstance(JDBCValue.class);
						value.init(columns[i-1], resultSet.getString(i));
						values.add(value);
					}
					JDBCLine line = factory.newInstance(JDBCLine.class);
					line.init(from, values);
					lines.add(line);
				}

				JDBCResultSet result = factory.newInstance(JDBCResultSet.class);
				result.init(from, lines);
				return result;
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
}
