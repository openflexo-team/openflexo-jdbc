package org.openflexo.technologyadapter.jdbc.util;

import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL request helper
 */
public class SQLHelper {

    public static final String SELECT_TABLES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA='PUBLIC'";

    /** Requests through SQL the list of table for one connection */
    public static List<JDBCTable> getTables(Connection connection, ModelFactory factory) throws SQLException {
        CallableStatement call = connection.prepareCall(SELECT_TABLES);
        call.execute();

        ResultSet resultSet = call.getResultSet();
        ArrayList<JDBCTable> tables = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            tables.add(factory.newInstance(JDBCTable.class));
        }
        return tables;
    }

    /*
    public static List<JDBCColumn> getTableColumns(String tableName, Connection connection, JDBCFactory factory) {

    }
    */

}
