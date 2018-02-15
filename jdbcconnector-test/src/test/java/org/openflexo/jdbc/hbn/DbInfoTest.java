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

package org.openflexo.jdbc.hbn;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.openflexo.technologyadapter.jdbc.model.DynamicModelBuilder;
import org.openflexo.technologyadapter.jdbc.model.Vehicle;

public class DbInfoTest extends HbnTest {

	private JdbcEnvironment jdbcEnv = null;
	private Metadata metadata;
	private Connection conn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// ******
		// Create temp schema
		conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);
		try (Statement stmt = conn.createStatement()) {
			stmt.executeQuery("drop table test if exists");
			stmt.executeQuery("create table test (id integer, nom char(16));");
		}
		// ******
		// Set up Hibernate configuration
		// adds a class with annotations
		config.addAnnotatedClass(Vehicle.class);

		// Creation du model
		DynamicModelBuilder modelBuilder = new DynamicModelBuilder(config);

		metadata = modelBuilder.buildDynamicModel();

	}

	@Override
	protected void tearDown() throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeQuery("drop table test if exists");
		conn.close();
		super.tearDown();
	}

	public void testPrintSchemaInfo() {

		System.out.println("*********** test1");

		assertNotNull(conn);

		try {

			DatabaseMetaData meta = conn.getMetaData();

			ResultSet schemas = meta.getSchemas();
			while (schemas.next()) {
				String tableSchema = schemas.getString(1); // "TABLE_SCHEM"
				String tableCatalog = schemas.getString(2); // "TABLE_CATALOG"
				System.out.println("tableSchema " + tableSchema);

				ResultSet tables = meta.getTables(tableCatalog, tableSchema, "%", null);
				while (tables.next()) {
					String val1 = tables.getString(1);
					String val2 = tables.getString(2);
					String val3 = tables.getString(3);
					System.out.println("tableSchema " + val1 + "-- " + val2 + "--" + val3);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
