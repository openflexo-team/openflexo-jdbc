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
import org.openflexo.foundation.test.OpenflexoTestCase;
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

   private JDBCTable createTable3(String tableName, JDBCSchema schema) {
        String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
        String[] name = {"c1", "VARCHAR(100)"};
        String[] lastName = {"c2", "VARCHAR(100)"};
        String[] otherId = {"other_id", "INT"};
        return schema.createTable(tableName, id, name, lastName, otherId);
    }

	private JDBCConnection createJDBCMemoryConnection(String name) throws ModelDefinitionException {
		return createJDBCConnection("mem:" + name);
	}

	private JDBCConnection createJDBCConnection(String protocolAndName) throws ModelDefinitionException {
		JDBCFactory factory = new JDBCFactory(null, null);
		JDBCConnection connection = factory.newInstance(JDBCConnection.class);
		connection.setAddress("jdbc:hsqldb:" + protocolAndName);
		connection.setUser("SA");
		return connection;
	}

	@Test
    public void testLoad() throws Exception {

		JDBCFactory factory = new JDBCFactory(null, null);
		try (InputStream stream = new BufferedInputStream(getClass().getResourceAsStream("Test1.jdbc"))) {
			JDBCConnection result = (JDBCConnection) factory.deserialize(stream);
			assertNotNull(result);
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

		assertNotNull(table1.createColumn("other", "VARCHAR(100)", false));
		assertNotNull(table1.createColumn("other2", "INT", false));
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

	@Test
	public void testInsertAndSelectLines1() throws Exception {
		JDBCConnection connection = createJDBCMemoryConnection("insertLine");
		JDBCTable table1 = createTable1("table1", connection.getSchema());

		// insert some values
		assertTrue(table1.insert(new String[]{"ID", "1", "NAME", "toto1"}));
		assertTrue(table1.insert(new String[]{"ID", "2", "NAME", "toto2"}));
		assertTrue(table1.insert(new String[]{"ID", "3", "NAME", "toto3"}));
		assertTrue(table1.insert(new String[]{"ID", "4", "NAME", "toto4"}));

		// insert existing value, must fail
		assertFalse(table1.insert(new String[]{"ID", "2", "NAME", "toto"}));

		JDBCResultSet result = table1.selectAll();
		assertNotNull(result);

		assertEquals(4, result.getLines().size());

		JDBCResultSet toto1Result = table1.select("name='toto1'");
		assertNotNull(toto1Result);
		assertEquals(1, toto1Result.getLines().size());
		JDBCLine line = toto1Result.getLines().get(0);
		assertEquals(2, line.getValues().size());
		JDBCValue value = line.getValues().get(1);
		assertEquals("toto1", value.getValue());
	}

	@Test
	public void testUpdateValues() throws Exception {
		JDBCConnection connection = createJDBCMemoryConnection("updateValues");
		JDBCTable table1 = createTable1("table1", connection.getSchema());
		assertTrue(table1.insert(new String[]{"ID", "1", "NAME", "toto1"}));

		JDBCResultSet set = table1.selectAll();

		JDBCValue value = set.find("1").getValues().get(1);
		assertEquals("toto1", value.getValue());
		assertTrue(value.setValue("test2"));
		assertEquals("test2", value.getValue());

		// selects the value again
		value = table1.select("id=1").getLines().get(0).getValues().get(1);
		assertEquals("test2", value.getValue());
	}

	@Test
	public void testJoin() throws Exception {
		JDBCConnection connection = createJDBCMemoryConnection("join");
		JDBCSchema schema = connection.getSchema();
		JDBCTable t1 = createTable1("t1", schema);
		t1.insert(new String[]{"ID", "1", "NAME", "name1"});
		t1.insert(new String[]{"ID", "2", "NAME",  "name2"});

		JDBCTable t2 = createTable3("t2", schema);
		t2.insert(new String[]{"ID", "1", "C1", "value1", "C2", "string1", "OTHER_ID", "1"});
		t2.insert(new String[]{"ID", "2", "C1", "value2", "C2", "string2", "OTHER_ID", "2"});
		t2.insert(new String[]{"ID", "3", "C1", "value3", "C2", "string3", "OTHER_ID", "1"});
		t2.insert(new String[]{"ID", "4", "C1", "value4", "C2", "string4", "OTHER_ID", "2"});
		t2.insert(new String[]{"ID", "5", "C1", "value5", "C2", "string5", "OTHER_ID", "1"});
		t2.insert(new String[]{"ID", "6", "C1", "value6", "C2", "string6", "OTHER_ID", "2"});
		t2.insert(new String[]{"ID", "7", "C1", "value7", "C2", "string7", "OTHER_ID", "1"});
		t2.insert(new String[]{"ID", "8", "C1", "value8", "C2", "string8", "OTHER_ID", "2"});
		t2.insert(new String[]{"ID", "9", "C1", "value9", "C2", "string9", "OTHER_ID", "1"});
		t2.insert(new String[]{"ID", "10", "C1", "value10", "C2", "string10", "OTHER_ID", "2"});

		JDBCResultSet resultSet = t1.selectAllWithJoin("InnerJoin", t1.getColumn("id"), t2.getColumn("other_id"));
		assertNotNull(resultSet);
		assertEquals(10, resultSet.getLines().size());

		resultSet = t1.selectWithJoin("InnerJoin", t1.getColumn("id"), t2.getColumn("other_id"), "t1.id = 1");
		assertNotNull(resultSet);
		assertEquals(5, resultSet.getLines().size());
	}
}
