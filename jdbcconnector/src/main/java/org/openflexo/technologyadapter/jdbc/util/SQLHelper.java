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


	public static ModelFactory getFactory(JDBCConnection model) {
		// Find the correct factory
		ModelFactory factory = null;

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
					JDBCTable table = factory.newInstance(JDBCTable.class);
					String tableName = resultSet.getString("TABLE_NAME");
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

    public static void dropTable(
    		final JDBCTable table, final ModelFactory factory
	) throws SQLException {

	}

}
