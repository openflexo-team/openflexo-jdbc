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

package org.openflexo.jdbc.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.technologyadapter.jdbc.HbnConfig;
import org.openflexo.technologyadapter.jdbc.model.Vehicle;
import org.openflexo.test.OnlyOnWindowsRunner;

/**
 * Testing standard Hobernage Mapping with annotated class
 * 
 * @author xtof
 *
 */

@RunWith(OnlyOnWindowsRunner.class)
public class SQLServerBasicTest extends SQLServerTestCase {

	protected final static String jdbcURL = "jdbc:sqlserver://localhost;databaseName=nessy_guyot;";
	protected final static String jdbcUser = "sa";
	protected final static String jdbcPwd = "bonjour";

	private EntityManager hbnEM;
	private HbnConfig config;

	@Before
	public void setUp() throws Exception {
		config = createHbnConfig(jdbcDriverClassname, jdbcURL, jdbcUser, jdbcPwd, hbnDialect);
		config.addAnnotatedClass(Vehicle.class);
		// creates object, wipe out if already exists
		config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hbnEM = config.createEntityManager();
	}

	@Override
	@After
	public void tearDown() throws Exception {

		// Close session
		hbnEM.close();

		super.tearDown();
	}

	@Test
	public void testCreateData() {

		System.out.println("*********** testCreateData");

		// adds a class with annotations

		try (SessionFactory hbnSessionFactory = config.getSessionFactory()) {
			EntityManager hbnEM = hbnSessionFactory.createEntityManager();
			try (Session hbnSession = hbnSessionFactory.withOptions().openSession()) {

				// Hibernate native
				Transaction trans = hbnSession.beginTransaction();
				hbnSession.save(new Vehicle(1, "A", "EEB75"));
				hbnSession.save(new Vehicle(2, "B", "A54B85"));
				trans.commit();

				// JPA Entity Manager
				hbnEM.getTransaction().begin();
				hbnEM.persist(new Vehicle(3, "C", "Prout"));
				hbnEM.persist(new Vehicle(4, "D", "Pouet"));
				hbnEM.getTransaction().commit();

				// Standard SQL
				NativeQuery<?> sqlQ = hbnSession.createNativeQuery("select * from T_Vehicles;");
				List<?> result = sqlQ.getResultList();
				for (Object o : result) {
					System.out.println("o=" + o + " of " + o.getClass());
					if (o.getClass().isArray()) {
						Object[] array = (Object[]) o;
						for (Object o2 : array) {
							System.out.println("> " + o2);
						}
					}
				}
				assertEquals(result.size(), 4);

				// Close session
				hbnEM.close();
			}
		}
	}

}
