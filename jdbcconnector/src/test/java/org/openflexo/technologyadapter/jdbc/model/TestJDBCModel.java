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
 *          of EPL 1.0, the licensors of this Program grantAllOn you additional permission
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.OpenflexoTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.test.OrderedRunner;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Test JDBC model
 * 
 * @author charlie
 * 
 */
@RunWith(OrderedRunner.class)
public class TestJDBCModel extends OpenflexoTestCase {

	// TODO: test drop table
	// TODO: test drop column

    private JDBCTable createTable1(String tableName, JDBCSchema schema) {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
		return schema.createTable(tableName, id, name);
    }

    private JDBCTable createTable2(String tableName, JDBCSchema schema) {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
        String[] lastName = {"lastname", "VARCHAR(100)"};
        String[] description = {"description", "VARCHAR(500)"};
        String[] portrait = {"portrait", "VARCHAR(200)"};
        return schema.createTable(tableName, id, name, lastName, description, portrait);
    }

	private JDBCConnection createJDBCMemoryConnection(String name) throws ModelDefinitionException {
		JDBCFactory factory = new JDBCFactory(null, null);
		JDBCConnection connection = factory.newInstance(JDBCConnection.class);
		connection.setAddress("jdbc:hsqldb:mem:" + name);
		connection.setUser("SA");
		return connection;
	}

	@Test
    public void testTables1() throws Exception {

		JDBCFactory factory = new JDBCFactory(null, null);
		try (InputStream stream = new BufferedInputStream(getClass().getResourceAsStream("Test1.jdbc"))) {
			JDBCConnection result = (JDBCConnection) factory.deserialize(stream);
			JDBCSchema schema = result.getSchema();

			assertEquals(0, schema.getTables().size());

			String tableName1 = "test1";
			assertNotNull(createTable1(tableName1, schema));
			assertEquals(1, schema.getTables().size());
			assertEquals(2, schema.getTable(tableName1).getColumns().size());

			String tableName2 = "TEST2";
			assertNotNull(createTable2(tableName2, schema));
			assertEquals(2, schema.getTables().size());
			assertEquals(5, schema.getTable(tableName2).getColumns().size());
		}
    }

    @Test
	public void testCreateColumns() throws Exception {
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL" };
		String[] name = { "name", "VARCHAR(100)" };
		String[] description = { "description", "VARCHAR(1512)" };
		String tableName = "testCreateColumns_table1";

		JDBCConnection connection = createJDBCMemoryConnection("createColumns//localhost/");

		JDBCSchema schema = connection.getSchema();

		JDBCTable table1 = schema.createTable(tableName, id, name, description);
		assertNotNull(table1);


		assertTrue(table1.grant("ALL", connection.getUser()));

		assertNotNull(table1.createColumn("other", "VARCHAR(100)"));
		assertNotNull(table1.createColumn("other2", "INT"));
	}

	@Test
	public void testDropColumns() throws Exception {
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL" };
		String[] name = { "name", "VARCHAR(100)" };
		String[] description = { "description", "VARCHAR(1512)" };
		String tableName = "testDropColumns_table1";

		JDBCConnection connection = createJDBCMemoryConnection("dropColumns");

		JDBCSchema schema = connection.getSchema();

		JDBCTable table1 = schema.createTable(tableName, id, name, description);
		assertNotNull(table1);

		assertTrue(table1.grant("ALL", connection.getUser()));

		JDBCColumn column = table1.getColumn("name");
		assertNotNull(column);
		assertTrue(table1.dropColumn(column));

		column = table1.getColumn("description");
		assertNotNull(column);
		assertTrue(table1.dropColumn(column));

	}
}
