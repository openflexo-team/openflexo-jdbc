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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.hbn.HbnEntityBindingModel;
import org.openflexo.connie.hbn.HibernateBindingFactory;
import org.openflexo.hbn.test.HbnTest;
import org.openflexo.hbn.test.model.Vehicle;

public class BasicBindingFactoryTest extends HbnTest {

	private final Map<EntityType<?>, HbnEntityBindingModel> modelsMap = new HashMap<EntityType<?>, HbnEntityBindingModel>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		MetadataSources hbnMetadataSrc = new MetadataSources(hbnRegistry);

		// adds a class with annotations
		hbnMetadataSrc.addAnnotatedClass(Vehicle.class);

		// Creates Hbn Session
		Configuration hbnConfig = new Configuration(hbnMetadataSrc);
		SessionFactory hbnSessionFactory = hbnConfig.buildSessionFactory(hbnRegistry);
		hbnSession = hbnSessionFactory.withOptions().openSession();
		bindingFactory = new HibernateBindingFactory(hbnSession.getMetamodel());
		bindingContext = new TestBindingContext(bindingFactory);
	}

	@Override
	protected void tearDown() throws Exception {

		hbnSession.close();

		super.tearDown();
	}

	public void testListBindingVariables() {

		System.out.println("*********** testListBindingVariables");

		assertNotNull(hbnSession);

		// Explore le metamodel
		Set<EntityType<?>> entities = hbnSession.getMetamodel().getEntities();
		for (EntityType ent : entities) {
			HbnEntityBindingModel bm = modelsMap.get(ent);
			if (bm == null) {
				bm = new HbnEntityBindingModel(ent);
				bm.updateVariables();
				modelsMap.put(ent, bm);
			}
			List<BindingVariable> listVariables = bm.getAccessibleBindingVariables();

			System.out.println("For Entity: " + ent.getName());
			for (BindingVariable bv : listVariables) {
				System.out.println("     var: " + bv.getLabel() + " -> " + bv.getType().toString());
			}
			assertEquals(4, listVariables.size());

		}
	}

	public void testBinding1() {

		System.out.println("*********** testBinding1");

		assertNotNull(hbnSession);
		assertNotNull(bindingFactory);
		assertNotNull(bindingContext);

		genericTest("org.openflexo.hbn.test.model.Vehicle", String.class, "toto");

	}

}
