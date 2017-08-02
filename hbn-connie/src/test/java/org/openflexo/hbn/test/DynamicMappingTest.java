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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.query.NativeQuery;
import org.openflexo.hbn.test.model.DynamicModelBuilder;

public class DynamicMappingTest extends HbnTest {

	private DynamicModelBuilder modelBuilder;
	private Session hbnSession;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Creation du model
		DynamicModelBuilder modelBuilder = new DynamicModelBuilder(hbnRegistry);
		Metadata metadata = modelBuilder.buildDynamicModel();

		// Creation de la session

		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		hbnSession = hbnSessionFactory.withOptions().openSession();
	}

	@Override
	protected void tearDown() throws Exception {

		// Close session
		hbnSession.close();

		super.tearDown();
	}

	public void testDynamicMapping() {

		System.out.println("*********** testDynamicMapping");

		assertNotNull(hbnSession);

		// Cr�ation de l'instance

		Map<String, String> syl = new HashMap<>();
		syl.put("Nom", "Sylvain");
		Map<String, String> chris = new HashMap<>();
		chris.put("Nom", "Guychard");
		chris.put("Prenom", "Christophe");

		// Sérialisation de l'instance
		// Hibernate native
		Transaction trans = hbnSession.beginTransaction();

		hbnSession.save("Dynamic_Class", syl);
		hbnSession.save("Dynamic_Class", chris);

		trans.commit();

		// Standard SQL
		NativeQuery<?> sqlQ = hbnSession.createNativeQuery("select * from T_Dynamic_Table;");
		List<?> result = sqlQ.getResultList();
		assertEquals(2, result.size());
		sqlQ = hbnSession.createNativeQuery("select * from T_Dynamic_Table where padam is null;");
		result = sqlQ.getResultList();
		assertEquals(1, result.size());

		// Explore le metamodel
		Set<EntityType<?>> entities = hbnSession.getMetamodel().getEntities();
		for (EntityType<?> ent : entities) {
			System.out.println("Entité dynamique: " + ent.getName());
		}

	}

}
