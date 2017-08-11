/**
 * 
 * Copyright (c) 2017, Openflexo
 * This file is part of Connie-core, a component of the software infrastructure 
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

package org.openflexo.hbn.test;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.openflexo.connie.hbn.HbnConfig;

import junit.framework.TestCase;

/**
 * Provides an environment with a connection to an HSQLDB database
 * 
 * @author sylvain
 *
 */
public abstract class HbnTest extends TestCase {

	// JDBC configuration to use HSQLdb

	protected final static String jdbcURL = "jdbc:hsqldb:data/hbnTests";
	protected final static String jdbcDriverClassname = "org.hsqldb.jdbcDriver";
	protected final static String jdbcUser = "sa";
	protected final static String jdbcPwd = "";

	// Hibernate configuration

	protected final static String hbnDialect = "org.hibernate.dialect.HSQLDialect";

	protected HbnConfig config = null;

	// Connie configuration

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Loads JdbcDriver
		Class.forName(jdbcDriverClassname);
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);
		} catch (Exception e) {
			fail("Cannot connect to DB: " + jdbcURL);
			e.printStackTrace();
			conn = null;
		}
		if (conn != null) {
			System.out.println("Got Connection.");

			// Setup Hibernate config

			config = new HbnConfig(new BootstrapServiceRegistryBuilder().build());

			config.setProperty("hibernate.connection.driver_class", jdbcDriverClassname);
			config.setProperty("hibernate.connection.url", jdbcURL);
			config.setProperty("hibernate.connection.username", jdbcUser);
			config.setProperty("hibernate.connection.password", jdbcPwd);
			config.setProperty("hibernate.connection.pool_size", "1");
			config.setProperty("hibernate.dialect", hbnDialect);
			config.setProperty("hibernate.show_sql", "true");
			// creates object, wipe out if already exists
			config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

			conn.close();
		}

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
