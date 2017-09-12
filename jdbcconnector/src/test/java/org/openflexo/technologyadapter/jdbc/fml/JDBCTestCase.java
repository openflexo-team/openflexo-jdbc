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

package org.openflexo.technologyadapter.jdbc.fml;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.ModelUtils;

/**
 * Provides testing environment in JDBC context
 *
 */
public abstract class JDBCTestCase extends OpenflexoProjectAtRunTimeTestCase {

	protected static JDBCConnection connection;

	@AfterClass
	public static void tearDownClass() {
		deleteProject();
		deleteTestResourceCenters();
		unloadServiceManager();
	}

	@BeforeClass
	public static void setupClass() throws ModelDefinitionException {
		serviceManager = instanciateTestServiceManager(JDBCTechnologyAdapter.class);
		connection = prepareDatabase("db");
	}

	protected static JDBCConnection prepareDatabase(String name) throws ModelDefinitionException {
		JDBCConnection connection = ModelUtils.createJDBCMemoryConnection(name);
		/*JDBCTable table1 = ModelUtils.createTable1("table1", connection.getSchema());
		ModelUtils.addLinesForTable1(table1);
		ModelUtils.createTable2("table2", connection.getSchema());
		JDBCTable table3 = ModelUtils.createTable3("table3", connection.getSchema());
		ModelUtils.addLinesForTable3(table3);*/
		return connection;
	}

	protected static JDBCTable createTable(String tableName, String[]... attributes) {
		return connection.getSchema().createTable(tableName, attributes);
	}

	protected static String[] createPrimaryKeyIntegerAttribute(String name) {
		String[] returned = { name, "INT", "PRIMARY KEY", "NOT NULL", "IDENTITY" };
		return returned;
	}

	protected static String[] createForeignKeyIntegerAttribute(String name) {
		String[] returned = { name, "INT", "NOT NULL" };
		return returned;
	}

	protected static String[] createStringAttribute(String name, int length) {
		String[] returned = { name, "VARCHAR(" + length + ")" };
		return returned;
	}

	protected static String[] createDateAttribute(String name) {
		String[] returned = { name, "DATE" };
		return returned;
	}

	public void debugTable(Session session, String entityName) {
		NativeQuery<?> sqlQ = session.createNativeQuery("select * from " + entityName + ";");
		System.out.println("select * from " + entityName + ";");
		for (Object rowResult : sqlQ.getResultList()) {
			if (rowResult.getClass().isArray()) {
				StringBuffer sb = new StringBuffer();
				for (Object o : (Object[]) rowResult) {
					sb.append(" " + o);
				}
				System.out.println(" > " + sb.toString());
			}
		}
	}
}
