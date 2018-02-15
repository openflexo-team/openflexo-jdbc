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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

public class HashMapTest extends HbnTest {

	private Session hbnSession;
	private SessionFactory hbnSessionFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// adds a class with annotations
		config.addResource("/org/openflexo/hbn/test/vehicle.xml");

		hbnSessionFactory = config.getSessionFactory();
		hbnSession = hbnSessionFactory.withOptions().openSession();

		// bindingFactory = new JpaBindingFactory(hbnSession.getMetamodel());

		// entityManager = new EntityManagerCtxt(bindingFactory, hbnSession);
	}

	@Override
	protected void tearDown() throws Exception {
		// Close session
		hbnSession.close();
		hbnSessionFactory.close();

		super.tearDown();
	}

	public void test1() {

		System.out.println("*********** test1");

		// Hibernate native
		Transaction trans = hbnSession.beginTransaction();

		Map<String, Object> record = new HashMap<>();
		record.put("number", 1);
		record.put("name", "A");
		record.put("mineralogic", "EEB75");
		hbnSession.save("Vehicle", record);

		record = new HashMap<>();
		record.put("number", 2);
		record.put("name", "B");
		record.put("mineralogic", "A54B85");
		hbnSession.save("Vehicle", record);

		record = new HashMap<>();
		record.put("number", 3);
		record.put("name", "C");
		record.put("mineralogic", "Prout");
		hbnSession.save("Vehicle", record);

		record = new HashMap<>();
		record.put("number", 4);
		record.put("name", "D");
		record.put("mineralogic", "Pouet");
		hbnSession.save("Vehicle", record);

		trans.commit();

		// Standard SQL
		NativeQuery<?> sqlQ = hbnSession.createNativeQuery("select * from Vehicle;");
		List<?> result = sqlQ.getResultList();
		assertEquals(4, result.size());

		// Close session
		hbnSession.close();
	}

	/*public void testBindingModel() {
	
		System.out.println("*********** testBindingModel");
	
		assertNotNull(hbnSession);
		assertNotNull(bindingFactory);
		assertNotNull(entityManager.getBindingModel());
	
		assertEquals(2, entityManager.getBindingModel().getBindingVariablesCount());
	
	}
	
	public void testBindings() {
	
		System.out.println("*********** testBindings");
	
		genericTest(entityManager, "self", EntityManagerCtxt.class, entityManager);
		genericTest(entityManager, "self.Vehicle", EntityType.class, null);
		genericTest(entityManager, "self.Vehicle.mineralogic", Attribute.class, null);
	}*/
}
