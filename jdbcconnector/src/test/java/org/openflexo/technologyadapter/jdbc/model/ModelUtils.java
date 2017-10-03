/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

import static org.junit.Assert.assertNotNull;

import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Sets of utility methods
 */
public class ModelUtils {
	public static JDBCTable createTable1(String tableName, JDBCSchema schema) {
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL", "IDENTITY" };
		String[] name = { "name", "VARCHAR(100)" };
		return schema.createTable(tableName, id, name);
	}

	public static JDBCTable createTable2(String tableName, JDBCSchema schema) {
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL", "IDENTITY" };
		String[] name = { "name", "VARCHAR(100)" };
		String[] lastName = { "lastname", "VARCHAR(100)" };
		String[] description = { "description", "VARCHAR(500)" };
		String[] portrait = { "portrait", "VARCHAR(200)" };
		return schema.createTable(tableName, id, name, lastName, description, portrait);
	}

	public static JDBCTable createTable3(String tableName, JDBCSchema schema) {
		String[] id = { "id", "INT", "PRIMARY KEY", "NOT NULL" };
		String[] name = { "c1", "VARCHAR(100)" };
		String[] lastName = { "c2", "VARCHAR(100)" };
		String[] otherId = { "other_id", "INT" };
		return schema.createTable(tableName, id, name, lastName, otherId);
	}

	public static JDBCConnection createJDBCMemoryConnection(String name) throws ModelDefinitionException {
		return createJDBCHSQLConnection("mem:" + name);
	}

	public static JDBCConnection createJDBCHSQLConnection(String protocolAndName) throws ModelDefinitionException {
		JDBCFactory factory = new JDBCFactory(null, null);
		JDBCConnection connection = factory.newInstance(JDBCConnection.class);
		connection.setDbType(JDBCDbType.HSQLDB);
		connection.setAddress("jdbc:hsqldb:" + protocolAndName);
		connection.setUser("SA");
		connection.getConnection();
		return connection;
	}

	public static void addLinesForTable1(JDBCTable table) {
		assertNotNull(table.insert(new String[] { "ID", "1", "NAME", "toto1" }));
		assertNotNull(table.insert(new String[] { "ID", "2", "NAME", "toto2" }));
		assertNotNull(table.insert(new String[] { "ID", "3", "NAME", "toto3" }));
		assertNotNull(table.insert(new String[] { "ID", "4", "NAME", "toto4" }));
	}

	public static void addLinesForTable3(JDBCTable table) {
		assertNotNull(table.insert(new String[] { "ID", "1", "C1", "value1", "C2", "string1", "OTHER_ID", "1" }));
		assertNotNull(table.insert(new String[] { "ID", "2", "C1", "value2", "C2", "string2", "OTHER_ID", "2" }));
		assertNotNull(table.insert(new String[] { "ID", "3", "C1", "value3", "C2", "string3", "OTHER_ID", "1" }));
		assertNotNull(table.insert(new String[] { "ID", "4", "C1", "value4", "C2", "string4", "OTHER_ID", "2" }));
		assertNotNull(table.insert(new String[] { "ID", "5", "C1", "value5", "C2", "string5", "OTHER_ID", "1" }));
		assertNotNull(table.insert(new String[] { "ID", "6", "C1", "value6", "C2", "string6", "OTHER_ID", "2" }));
		assertNotNull(table.insert(new String[] { "ID", "7", "C1", "value7", "C2", "string7", "OTHER_ID", "1" }));
		assertNotNull(table.insert(new String[] { "ID", "8", "C1", "value8", "C2", "string8", "OTHER_ID", "2" }));
		assertNotNull(table.insert(new String[] { "ID", "9", "C1", "value9", "C2", "string9", "OTHER_ID", "1" }));
		assertNotNull(table.insert(new String[] { "ID", "10", "C1", "value10", "C2", "string10", "OTHER_ID", "2" }));
	}

}
