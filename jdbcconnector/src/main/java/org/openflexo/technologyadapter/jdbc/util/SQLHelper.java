package org.openflexo.technologyadapter.jdbc.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

import java.sql.Connection;
import java.sql.ResultSet;
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

	public static final String DROP_TABLE = "DROP TABLE ";

	public static final String ADD_COLUMN = "ALTER TABLE ? ADD ? ?";
	public static final String DROP_COLUMN = "ALTER TABLE ? DROP COLUMN ?";

	private static ResultSetHandler<Object> NO_OP = new ResultSetHandler<Object>() {
		@Override
		public Object handle(ResultSet rs) throws SQLException {
			return null;
		}
	};

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

    public static List<JDBCColumn> getTableColumns(JDBCTable table, final ModelFactory factory) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
		return new QueryRunner().query(connection, SELECT_COLUMNS, new ResultSetHandler<List<JDBCColumn>>() {
			@Override
			public List<JDBCColumn> handle(ResultSet resultSet) throws SQLException {
				ArrayList<JDBCColumn> columns = new ArrayList<>();
				while (resultSet.next()) {
					JDBCColumn column = factory.newInstance(JDBCColumn.class);
					column.init(resultSet.getString(1), resultSet.getString(2));
					columns.add(column);
				}
				return columns;
			}
		}, table.getName());
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
		request.append(name);
		request.append(" (");
		int length = request.length();

		for (String[] attribute : attributes) {
			if (length < request.length()) request.append(", ");

			int localLength = request.length();
			for (String part : attribute) {
				if (localLength < request.length()) request.append(" ");
				request.append(part);
			}
		}
		request.append(")");

		return request.toString();
	}


	public static void dropTable(
    		final JDBCSchema schema, final String tableName
	) throws SQLException {
		Connection connection = schema.getModel().getConnection();
		new QueryRunner().insert(connection, DROP_TABLE + tableName, NO_OP);
	}

	public static JDBCColumn createColumn(
			final JDBCTable table, final ModelFactory factory, final String columnName, final String type
	) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
		return new QueryRunner().insert(connection, ADD_COLUMN, new ResultSetHandler<JDBCColumn>() {
			@Override
			public JDBCColumn handle(ResultSet resultSet) throws SQLException {
				JDBCColumn column = factory.newInstance(JDBCColumn.class);
				column.init(columnName, type);
				return column;
			}
		}, table.getName(), columnName, type);
	}

	public static void dropColumn(final JDBCTable table, final String columnName) throws SQLException {
		Connection connection = table.getSchema().getModel().getConnection();
		new QueryRunner().insert(connection, DROP_COLUMN, NO_OP, table.getName(), columnName);
	}

	public static void grant(JDBCConnection connection, String access, String on, String user) throws SQLException {
		String grantAll = "GRANT "+ access +" ON " + on + " TO " + user + "";
		new QueryRunner().insert(connection.getConnection(),grantAll, NO_OP);
	}
}
