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

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.openflexo.hbn.test.model.Vehicle;

public class BasicTest extends HbnTest {

	public void test1() {

		System.out.println("*********** test1");

		/* FROM : http://www.programcreek.com/java-api-examples/index.php?api=org.hibernate.boot.MetadataBuilder */

		MetadataSources hbnMetadataSrc = new MetadataSources(hbnRegistry);
		// adds a class with annotations
		hbnMetadataSrc.addAnnotatedClass(Vehicle.class);

		Configuration hbnConfig = new Configuration(hbnMetadataSrc);
		SessionFactory hbnSessionFactory = hbnConfig.buildSessionFactory(hbnRegistry);
		EntityManager hbnEM = hbnSessionFactory.createEntityManager();
		Session hbnSession = hbnSessionFactory.withOptions().openSession();

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
		assertEquals(result.size(), 4);

		// Close session
		hbnSession.close();
	}

}
