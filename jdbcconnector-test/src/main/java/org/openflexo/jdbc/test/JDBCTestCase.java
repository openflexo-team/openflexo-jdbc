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

package org.openflexo.jdbc.test;

import org.hibernate.Session;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.junit.AfterClass;
import org.openflexo.connie.hbn.HbnConfig;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Provides testing environment in JDBC context
 *
 */
public abstract class JDBCTestCase extends OpenflexoProjectAtRunTimeTestCase {

	@AfterClass
	public static void tearDownClass() {
		deleteProject();
		deleteTestResourceCenters();
		unloadServiceManager();
	}

	/**
	 *
	 * Setup hibernate configuration
	 * 
	 * @param jdbcDriverClassname
	 * @param jdbcURL
	 * @param jdbcUser
	 * @param jdbcPwd
	 * @param hbnDialect
	 * @return
	 */
	protected static HbnConfig createHbnConfig(String jdbcDriverClassname, String jdbcURL, String jdbcUser, String jdbcPwd,
			String hbnDialect) {

		HbnConfig config = new HbnConfig(new BootstrapServiceRegistryBuilder().build());

		config.setProperty("hibernate.connection.driver_class", jdbcDriverClassname);
		config.setProperty("hibernate.connection.url", jdbcURL);
		config.setProperty("hibernate.connection.username", jdbcUser);
		config.setProperty("hibernate.connection.password", jdbcPwd);
		config.setProperty("hibernate.connection.pool_size", "1");
		config.setProperty("hibernate.dialect", hbnDialect);
		config.setProperty("hibernate.show_sql", "true");

		return config;
	}

	protected static JDBCTable createTable(JDBCConnection connection, String tableName, String[]... attributes) {
		return connection.getSchema().createTable(tableName, attributes);
	}

	protected static void dropTable(JDBCConnection connection, JDBCTable table) {
		if (table != null)
		connection.getSchema().dropTable(table);
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
