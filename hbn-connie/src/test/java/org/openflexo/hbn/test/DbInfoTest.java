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
import java.sql.Statement;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.openflexo.hbn.test.model.DynamicModelBuilder;
import org.openflexo.hbn.test.model.Vehicle;

public class DbInfoTest extends HbnTest {

	private SessionFactory hbnSessionFactory = null;
	private JdbcEnvironment jdbcEnv = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// ******
		// Create temp schema
		Connection c = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);
		Statement stmt = c.createStatement();

		stmt.executeQuery("drop table test if exists");
		stmt.executeQuery("create table test (id integer, nom char(16));");

		stmt.close();
		c.close();

		// ******
		// Set up Hibernate configuration
		// adds a class with annotations
		config.addAnnotatedClass(Vehicle.class);

		// Creation du model
		DynamicModelBuilder modelBuilder = new DynamicModelBuilder(config);

		Metadata metadata = modelBuilder.buildDynamicModel();

		hbnSessionFactory = config.getSessionFactory();

		jdbcEnv = config.getJdbcEnv();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() {

		System.out.println("*********** test1");

		assertNotNull(jdbcEnv);
		assertNotNull(hbnSessionFactory);

		try {

			EntityManager hbnEM = hbnSessionFactory.createEntityManager();

			ExtractedDatabaseMetaData dbMetadata = jdbcEnv.getExtractedDatabaseMetaData();

			Database toto = config.getMetadata().getDatabase();

			/* DatabaseInformation dbInfo = new DatabaseInformationImpl(hbnRegistry, jdbcEnv,
					new DdlTransactionIsolatorNonJtaImpl(hbnRegistry,
							new JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess(connectionProvider)),
					jdbcEnv.getDefaultNamespace().getName());
			*/
			System.out.println("klong sur mer : " + dbMetadata.toString());

			/* TODO : explore 
			DdlTransactionIsolator transIso = null;
			Name nom = new Name(jdbcEnv.getCurrentCatalog(), jdbcEnv.getCurrentSchema());
			DatabaseInformationImpl dbInfo = new DatabaseInformationImpl(hbnBsRegistry, env, transIso, nom);
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
