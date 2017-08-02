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
import javax.persistence.metamodel.EntityType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.hbn.EntityManagerCtxt;
import org.openflexo.connie.hbn.JpaBindingFactory;
import org.openflexo.connie.hbn.JpaEntityWrapper;
import org.openflexo.connie.hbn.EntityManagerBindingModel;
import org.openflexo.hbn.test.HbnTest;
import org.openflexo.hbn.test.model.DynamicModelBuilder;

public class BindingFactoryTestOnDynamicModel extends HbnTest {

	private EntityManagerBindingModel jpaWrapper;
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

		bindingContext = new EntityManagerCtxt(em);
		jpaWrapper = new EntityManagerBindingModel(bindingFactory, em.getMetamodel());
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
			JpaEntityWrapper bm = new JpaEntityWrapper(bindingFactory, ent);
			List<BindingVariable> listVariables = bm.getAccessibleBindingVariables();

			System.out.println("For Entity: " + ent.getName());
			for (BindingVariable bv : listVariables) {
				System.out.println("     var: " + bv.getLabel() + " -> " + bv.getType().toString());
			}

			assertEquals(3, listVariables.size());
			assertEquals(3, bm.getBindingVariablesCount());
		}

		/* Object parentEntity = BINDING_FACTORY.makeSimplePathElement(bm, "data");
		Object listElements = BINDING_FACTORY.getAccessibleSimplePathElements(bm); */
	}

	public void testJpaWrapper() {

		System.out.println("*********** testBinding1");

		assertNotNull(em);
		assertNotNull(bindingFactory);
		assertNotNull(jpaWrapper);

		assertEquals(2, jpaWrapper.getBindingVariablesCount());

		genericTest(jpaWrapper, "self", EntityManagerCtxt.class, bindingContext);
		genericTest(jpaWrapper, "self.Dynamic_Class", Session.class, em);
	}
	/*
		public void testBinding1() {
	
			System.out.println("*********** testBinding1");
	
			assertNotNull(hbnSession);
			assertNotNull(bindingFactory);
			assertNotNull(jpaWrapper);
	
			genericTest(jpaWrapper, "self.Dynamic_Class.Nom", String.class, "toto");
			genericTest(jpaWrapper, "self.Dynamic_Class.Nom", String.class, "toto");
	
		}
	*/

}
