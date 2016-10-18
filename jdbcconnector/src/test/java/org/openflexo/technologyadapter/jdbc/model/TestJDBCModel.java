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

    private JDBCTable createTableTest1(JDBCSchema schema) {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
		return schema.createTable("TEST1", id, name);
    }

    private JDBCTable createTableTest2(JDBCSchema schema) {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"name", "VARCHAR(100)"};
        String[] lastName = {"lastname", "VARCHAR(100)"};
        String[] description = {"description", "VARCHAR(500)"};
        String[] portrait = {"portrait", "VARCHAR(200)"};
        return schema.createTable("TEST2", id, name, lastName, description, portrait);
    }

	@Test
    public void testTables1() throws Exception {

		JDBCFactory factory = new JDBCFactory(null, null);
		try (InputStream stream = new BufferedInputStream(getClass().getResourceAsStream("Test1.jdbc"))) {
			JDBCConnection result = (JDBCConnection) factory.deserialize(stream);
			JDBCSchema schema = result.getSchema();

			assertEquals(0, schema.getTables().size());

			assertNotNull(createTableTest1(schema));
			assertEquals(1, schema.getTables().size());
			assertEquals(2, schema.getTable("TEST1").getColumns().size());

			// can't create table with small case name
			assertNull(schema.createTable("smallCaseName"));

			assertNotNull(createTableTest2(schema));
			assertEquals(2, schema.getTables().size());
			assertEquals(5, schema.getTable("TEST2").getColumns().size());
		}
    }

    @Test
	public void testColumns1() throws Exception {
		JDBCFactory factory = new JDBCFactory(null, null);

		JDBCConnection connection = factory.newInstance(JDBCConnection.class);
		connection.setAddress("jdbc:hsqldb:mem:.");
		connection.setUser("sa");

		JDBCSchema schema = connection.getSchema();
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL" };
		JDBCTable table1 = schema.createTable("TABLE1", id);
		assertNotNull(table1);

		assertTrue(connection.grantOn("ALL", table1));

		JDBCColumn column = table1.createColumn("name", "VARCHAR(100)");
		assertNotNull(column);
	}

}
