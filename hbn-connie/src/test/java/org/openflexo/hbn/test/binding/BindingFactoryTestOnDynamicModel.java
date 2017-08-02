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

package org.openflexo.hbn.test.binding;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.hbn.EntityBindingModel;
import org.openflexo.connie.hbn.EntityManagerCtxt;
import org.openflexo.connie.hbn.JpaBindingFactory;
import org.openflexo.hbn.test.HbnTest;
import org.openflexo.hbn.test.model.DynamicModelBuilder;

public class BindingFactoryTestOnDynamicModel extends HbnTest {

	private EntityManager em = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Creation du model
		DynamicModelBuilder modelBuilder = new DynamicModelBuilder(hbnRegistry);
		Metadata metadata = modelBuilder.buildDynamicModel();

		// Creation de la session

		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		em = hbnSessionFactory.createEntityManager();
		bindingFactory = new JpaBindingFactory(em.getMetamodel());

		entityManager = new EntityManagerCtxt(bindingFactory, em);
	}

	@Override
	protected void tearDown() throws Exception {

		// Close session
		em.close();

		super.tearDown();
	}

	public void testListEntityWrapperBindingVariables() {

		System.out.println("*********** testListEntityWrapperBindingVariables");

		assertNotNull(em);
		assertNotNull(bindingFactory);

		// Explore le metamodel
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		for (EntityType ent : entities) {
			EntityBindingModel bm = new EntityBindingModel(bindingFactory, ent);
			List<BindingVariable> listVariables = bm.getAccessibleBindingVariables();

			System.out.println("For Entity: " + ent.getName());
			for (BindingVariable bv : listVariables) {
				System.out.println("     var: " + bv.getLabel() + " -> " + bv.getType().toString());
			}

			assertEquals(3, listVariables.size());
			assertEquals(3, bm.getBindingVariablesCount());
		}

	}

	public void testBindingModel() {

		System.out.println("*********** testBinding1");

		assertNotNull(em);
		assertNotNull(bindingFactory);
		assertNotNull(entityManager.getBindingModel());

		assertEquals(3, entityManager.getBindingModel().getBindingVariablesCount());

		genericTest(entityManager, "self", EntityManagerCtxt.class, entityManager);
		genericTest(entityManager, "self.Dynamic_Class", EntityType.class, null);
		genericTest(entityManager, "self.Adresse", EntityType.class, null);
	}

	public void testBinding1() {

		System.out.println("*********** testBinding1");

		assertNotNull(em);
		assertNotNull(bindingFactory);
		assertNotNull(entityManager.getBindingModel());

		genericTest(entityManager, "self.Dynamic_Class.Nom", Attribute.class, null);
	}

	public void testBinding2() {

		System.out.println("*********** testBinding1");

		assertNotNull(em);
		assertNotNull(bindingFactory);
		assertNotNull(entityManager.getBindingModel());

		genericTest(entityManager, "self.Dynamic_Class.Prenom", Attribute.class, null);
	}

	public void testBinding3() {

		System.out.println("*********** testBinding1");

		assertNotNull(em);
		assertNotNull(bindingFactory);
		assertNotNull(entityManager.getBindingModel());

		genericTest(entityManager, "self.Adresse.Identifiant", Attribute.class, null);
	}

}
