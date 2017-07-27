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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.type.TypeResolver;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.hbn.HbnEntityBindingModel;
import org.openflexo.connie.hbn.HibernateBindingFactory;
import org.openflexo.hbn.test.HbnTest;

public class BindingFactoryTestOnDynamicModel extends HbnTest {

	private Session hbnSession = null;

	private static final BindingFactory BINDING_FACTORY = new HibernateBindingFactory();

	private final Map<EntityType<?>, HbnEntityBindingModel> modelsMap = new HashMap<EntityType<?>, HbnEntityBindingModel>();

	public static class TestBindingContext extends DefaultBindable implements BindingEvaluationContext {

		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}

		@Override
		public BindingModel getBindingModel() {
			return null;
		}

		@Override
		public Object getValue(BindingVariable variable) {

			return null;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		MetadataBuildingOptionsImpl buildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(hbnRegistry);
		InFlightMetadataCollectorImpl metadataCollector = new InFlightMetadataCollectorImpl(buildingOptions, new TypeResolver());
		ClassLoaderAccessImpl classLoaderAccess = new ClassLoaderAccessImpl(null, hbnRegistry);

		MetadataBuildingContextRootImpl metadataBuildingContext = new MetadataBuildingContextRootImpl(buildingOptions, classLoaderAccess,
				metadataCollector);

		Metadata metadata = metadataCollector.buildMetadataInstance(metadataBuildingContext);

		// Creation / Définition de la table
		Table table = metadataCollector.addTable("", "", "T_Dynamic_Table", null, false);
		table.setName("T_Dynamic_Table");
		Column col = new Column();
		col.setName("pouet");
		col.setLength(256);
		col.setSqlType("CHAR(256)");
		col.setUnique(true);
		table.addColumn(col);
		UniqueKey uk = new UniqueKey();
		uk.setTable(table);
		uk.addColumn(col);
		table.addUniqueKey(uk);

		Column col2 = new Column();
		col2.setName("padam");
		col2.setLength(256);
		col2.setSqlType("CHAR(256)");
		col2.setNullable(true);
		table.addColumn(col2);

		// Creation de l'entité persistée

		RootClass pClass = new RootClass(metadataBuildingContext);
		pClass.setEntityName("Dynamic_Class");
		pClass.setJpaEntityName("Dynamic_Class");
		pClass.setTable(table);
		metadataCollector.addEntityBinding(pClass);

		// Creation d'une propriété (clef) et son mapping

		Property prop = new Property();
		prop.setName("Nom");
		SimpleValue value = new SimpleValue((MetadataImplementor) metadata, table);
		value.setIdentifierGeneratorStrategy("assigned");
		value.setTypeName("java.lang.String");
		value.addColumn(col);
		value.setTable(table);
		prop.setValue(value);
		pClass.setIdentifier(value);
		pClass.setIdentifierProperty(prop);
		// pClass.addProperty(prop);

		// Creation d'une propriété (clef) et son mapping

		prop = new Property();
		prop.setName("Prenom");
		value = new SimpleValue((MetadataImplementor) metadata, table);
		value.setTypeName(String.class.getCanonicalName());
		value.addColumn(col2);
		value.setTable(table);
		prop.setValue(value);
		pClass.addProperty(prop);

		try {
			((MetadataImplementor) metadata).validate();
		} catch (MappingException e) {
			System.out.println("Validation Error: " + e.getMessage());
		}

		// Creation de la session

		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		hbnSession = hbnSessionFactory.withOptions().openSession();

	}

	@Override
	protected void tearDown() throws Exception {

		hbnSession.close();

		super.tearDown();
	}

	public void test1() {

		System.out.println("*********** test1");

		assertNotNull(hbnSession);

		/* FROM : http://www.programcreek.com/java-api-examples/index.php?api=org.hibernate.boot.MetadataBuilder */

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
		}
		/* Object parentEntity = BINDING_FACTORY.makeSimplePathElement(bm, "data");
		Object listElements = BINDING_FACTORY.getAccessibleSimplePathElements(bm); */
	}

}
