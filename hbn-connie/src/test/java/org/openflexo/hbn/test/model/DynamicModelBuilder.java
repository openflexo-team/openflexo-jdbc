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

package org.openflexo.hbn.test.model;

import org.hibernate.boot.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.type.TypeResolver;

public class DynamicModelBuilder {

	private StandardServiceRegistry registry;
	public MetadataBuildingOptionsImpl buildingOptions;
	public InFlightMetadataCollectorImpl metadataCollector;
	public ClassLoaderAccessImpl classLoaderAccess;

	public DynamicModelBuilder(StandardServiceRegistry hbnRegistry) {
		registry = hbnRegistry;
		buildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(hbnRegistry);
		metadataCollector = new InFlightMetadataCollectorImpl(buildingOptions, new TypeResolver());
		classLoaderAccess = new ClassLoaderAccessImpl(null, hbnRegistry);

	}

	public Metadata buildDynamicModel() {

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
		col.setNullable(false);
		table.addColumn(col);

		PrimaryKey pk = new PrimaryKey(table);
		pk.addColumn(col);

		UniqueKey uk1 = new UniqueKey();
		uk1.setName("Nom_Unique");
		uk1.setTable(table);
		uk1.addColumn(col);
		table.addUniqueKey(uk1);

		Column col2 = new Column();
		col2.setName("padam");
		col2.setLength(256);
		col2.setSqlType("CHAR(256)");
		col2.setNullable(true);
		table.addColumn(col2);
		// pour rire les couples "Nom + Prenom" doivent �tre uniques
		UniqueKey uk = new UniqueKey();
		uk.setName("Couple_Nom_Prenom_Unique");
		uk.setTable(table);
		uk.addColumn(col);
		uk.addColumn(col2);
		table.addUniqueKey(uk);

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
		value.setTypeName("java.lang.String");
		value.setIdentifierGeneratorStrategy("assigned");
		value.addColumn(col);
		value.setTable(table);
		prop.setValue(value);
		pClass.setDeclaredIdentifierProperty(prop);
		pClass.setIdentifierProperty(prop);
		pClass.setIdentifier(value);

		// Creation d'une propriété et son mapping

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

		return metadata;

	}
}
