/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
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

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.OpenflexoTestCase;
import org.openflexo.test.OrderedRunner;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test JDBC model
 * 
 * @author charlie
 * 
 */
@RunWith(OrderedRunner.class)
public class TestJDBCModel extends OpenflexoTestCase {

    /** Creates through SQL a table for one connection */
    private void createTable(Connection connection, String name, String[] ... attributes) throws SQLException {
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

        connection.prepareCall(request.toString()).execute();
    }


    private void createTableTest1(Connection connection) throws SQLException {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
        createTable(connection, "test1", id, name);
    }

    private void createTableTest2(Connection connection) throws SQLException {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
        String[] lastName = {"lastname", "VARCHAR(100)"};
        String[] description = {"description", "VARCHAR(500)"};
        String[] portrait = {"portrait", "VARCHAR(200)"};
        createTable(connection, "test2", id, name, lastName, description, portrait);
    }

    private Connection createAndPrepareConnection(String db, String user) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:"+db, user, "");

        // creates table
        createTableTest1(connection);
        createTableTest2(connection);

        return connection;
    }

	@Test
	public void test111() throws SQLException {
		Connection connection = createAndPrepareConnection("test1111", "user");

		// COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
		Object query = new QueryRunner().query(connection, "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?", new ResultSetHandler<Object>() {
			@Override
			public Object handle(ResultSet rs) throws SQLException {
				System.out.println(rs.getMetaData().getColumnCount());
				while (rs.next()) {
					System.out.println("- " + rs.getString(1) + "[" + rs.getString(2) + "]" );
				}


				return null;
			}
		}, "TEST1");

	}


    /**
	 * Test the diagram factory
	 */
	//@Test
	public void testInstanciateDiagram() throws Exception {
        /*
		DiagramFactory factory = new DiagramFactory(null, null);

		Diagram diagram = factory.newInstance(Diagram.class);
		assertTrue(diagram instanceof Diagram);

		DiagramShape shape1 = factory.makeNewShape("Shape1", ShapeType.RECTANGLE, new FGEPoint(100, 100), diagram);
		shape1.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.RED));
		shape1.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.BLUE));
		assertTrue(shape1 instanceof DiagramShape);
		DiagramShape shape2 = factory.makeNewShape("Shape2", ShapeType.RECTANGLE, new FGEPoint(200, 100), diagram);
		shape2.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.BLUE));
		shape2.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.WHITE));
		assertTrue(shape2 instanceof DiagramShape);

		DiagramConnector connector1 = factory.makeNewConnector("Connector", shape1, shape2, diagram);
		assertTrue(connector1 instanceof DiagramConnector);
        */

	}

	@Test
    public void testLoad1() throws Exception {
		createAndPrepareConnection("testLoad1", "user");

        JDBCFactory factory = new JDBCFactory(null, null);
        try (InputStream stream = new BufferedInputStream(getClass().getResourceAsStream("Test1.jdbc"))) {
            JDBCConnection result = (JDBCConnection) factory.deserialize(stream);
			result.connect();
			JDBCSchema schema = result.getSchema();
			System.out.println(schema);

			List<JDBCTable> tables = schema.getTables();
			assertEquals(2, tables.size());

			JDBCTable table = tables.get(0);
			System.out.println(table);
			assertEquals(2, table.getColumns().size());
		}
    }

}
