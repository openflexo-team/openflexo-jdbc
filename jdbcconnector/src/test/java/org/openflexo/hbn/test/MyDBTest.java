/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.TypeResolver;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.hbn.HbnConfig;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * 
 * 
 * @author sylvain
 * 
 */
// TODO: to be removed: used for experimentations
@RunWith(OrderedRunner.class)
@Ignore
public class MyDBTest extends OpenflexoTestCase {

	static FlexoEditor editor;

	static JDBCTechnologyAdapter jdbcTA;

	private static DirectoryResourceCenter resourceCenter;

	private static JDBCConnection connection;

	@Test
	@TestOrder(1)
	public void testCreateConnection() throws SaveResourceException, ModelDefinitionException, IOException, SQLException {
		instanciateTestServiceManager(JDBCTechnologyAdapter.class);

		resourceCenter = makeNewDirectoryResourceCenter();
		assertNotNull(resourceCenter);
		System.out.println("ResourceCenter= " + resourceCenter);

		jdbcTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
		assertNotNull(jdbcTA);

		JDBCResourceFactory jdbcResourceFactory = jdbcTA.getResourceFactory(JDBCResourceFactory.class);
		assertNotNull(jdbcResourceFactory);

		JDBCResource jdbcResource = jdbcTA.createResource(JDBCResourceFactory.class, resourceCenter, "JDBCConnection1",
				"JDBCConnection1.jdbc", "", JDBCResourceFactory.JDBC_EXTENSION, true);
		assertNotNull(jdbcResource);

		connection = jdbcResource.getLoadedResourceData();
		connection.setAddress("jdbc:hsqldb:hsql://localhost/");
		connection.setUser("sa");
		connection.setPassword("");

		if (connection.getConnection() == null) {
			fail(connection.getException().getMessage());
		}

		assertNotNull(connection.getConnection());

		assertNotNull(connection.getSchema());

		JDBCSchema schema = connection.getSchema();

		System.out.println("schema=" + schema);

	}

	@Test
	@TestOrder(2)
	public void testReadMetaModel() throws SQLException {

		DatabaseMetaData meta = connection.getConnection().getMetaData();

		ResultSet schemas = meta.getSchemas();
		while (schemas.next()) {
			System.out.println("****** Schema");
			String tableSchema = schemas.getString(1); // "TABLE_SCHEM"
			String tableCatalog = schemas.getString(2); // "TABLE_CATALOG"
			System.out.println("tableSchema " + tableSchema);

			ResultSet tables = meta.getTables(tableCatalog, tableSchema, "%", null);
			while (tables.next()) {
				String val1 = tables.getString(1);
				String val2 = tables.getString(2);
				String val3 = tables.getString(3);
				System.out.println("tableSchema " + val1 + "-- " + val2 + "--" + val3);
			}
		}
	}

	protected final static String jdbcURL = "jdbc:hsqldb:hsql://localhost/";
	protected final static String jdbcDriverClassname = "org.hsqldb.jdbcDriver";
	protected final static String jdbcUser = "sa";
	protected final static String jdbcPwd = "";

	protected final static String hbnDialect = "org.hibernate.dialect.HSQLDialect";

	protected static HbnConfig config;
	protected static MetadataBuildingOptionsImpl buildingOptions;
	protected static InFlightMetadataCollectorImpl metadataCollector;

	protected static Table table, table2;
	protected static Column col, col2, col3, col4, col5;

	protected static Metadata metadata;

	@Test
	@TestOrder(3)
	public void createHbnConfig() {

		config = new HbnConfig(new BootstrapServiceRegistryBuilder().build());

		config.setProperty("hibernate.connection.driver_class", jdbcDriverClassname);
		config.setProperty("hibernate.connection.url", jdbcURL);
		config.setProperty("hibernate.connection.username", jdbcUser);
		config.setProperty("hibernate.connection.password", jdbcPwd);
		config.setProperty("hibernate.connection.pool_size", "1");
		config.setProperty("hibernate.dialect", hbnDialect);
		config.setProperty("hibernate.show_sql", "true");
		// creates object, wipe out if already exists
		// config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

		buildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(config.getServiceRegistry());
		metadataCollector = new InFlightMetadataCollectorImpl(buildingOptions, new TypeResolver());

		/*final Namespace namespace = metadataCollector.getDatabase().locateNamespace(
				getDatabase().toIdentifier( catalogName ),
				getDatabase().toIdentifier( schemaName )
		);*/

		Iterable<Namespace> namespaces = metadataCollector.getDatabase().getNamespaces();

		System.out.println("Prout debut");
		for (Namespace ns : namespaces) {
			System.out.println("> hop: " + ns);
		}
		System.out.println("Prout fin");

		Namespace namespace = metadataCollector.getDatabase().getDefaultNamespace();

		for (JDBCTable table : connection.getSchema().getTables()) {
			System.out.println("Found table:  " + table + " hop: " + table.getName());
			Identifier logicalName = metadataCollector.getDatabase().toIdentifier(table.getName());
			System.out.println("logicalName=" + logicalName);
			Table laTable = namespace.locateTable(logicalName);
			System.out.println("latable=" + laTable);
		}

		System.out.println(namespace.getTables());

	}

	@Test
	@TestOrder(4)
	public void testDeclareJDBCMetaData() {

		table = metadataCollector.addTable("", "", "T_Dynamic_Table_2", null, false);
		table.setName("T_Dynamic_Table_2");
		col = new Column();
		col.setName("pouet");
		col.setLength(256);
		col.setSqlType("CHAR(32)");
		col.setNullable(false);
		table.addColumn(col);

		PrimaryKey pk = new PrimaryKey(table);
		pk.addColumn(col);

		UniqueKey uk1 = new UniqueKey();
		uk1.setName("Nom_Unique");
		uk1.setTable(table);
		uk1.addColumn(col);
		table.addUniqueKey(uk1);

		col2 = new Column();
		col2.setName("padam");
		col2.setLength(256);
		col2.setSqlType("CHAR(32)");
		col2.setNullable(true);
		table.addColumn(col2);
		// pour rire les couples "Nom + Prenom" doivent être uniques
		UniqueKey uk = new UniqueKey();
		uk.setName("Couple_Nom_Prenom_Unique");
		uk.setTable(table);
		uk.addColumn(col);
		uk.addColumn(col2);
		table.addUniqueKey(uk);

		// une colonne de clef etrangère vers T_Adresse
		col3 = new Column();
		col3.setName("id_addr");
		col3.setLength(16);
		col3.setSqlType("INTEGER");
		col3.setNullable(true);
		table.addColumn(col3);

		// **********
		// Creation / Définition de la table T_Adresse
		table2 = metadataCollector.addTable("", "", "T_Adresse_2", null, false);
		table2.setName("T_Adresse_2");
		col4 = new Column();
		col4.setName("Id");
		col4.setLength(16);
		col4.setSqlType("INTEGER");
		col4.setNullable(false);
		table2.addColumn(col4);

		pk = new PrimaryKey(table2);
		pk.addColumn(col);

		uk1 = new UniqueKey();
		uk1.setName("Id_Unique");
		uk1.setTable(table2);
		uk1.addColumn(col4);
		table.addUniqueKey(uk1);

		col5 = new Column();
		col5.setName("Adresse");
		col5.setLength(512);
		col5.setSqlType("CHAR(512)");
		col5.setNullable(true);
		table2.addColumn(col5);

	}

	@Test
	@TestOrder(5)
	public void testDeclareJDBCMapping() {

		MetadataBuildingContextRootImpl metadataBuildingContext = new MetadataBuildingContextRootImpl(buildingOptions,
				new ClassLoaderAccessImpl(null, config.getServiceRegistry()), metadataCollector);
		metadata = metadataCollector.buildMetadataInstance(metadataBuildingContext);

		// ************************
		// Creation de l'entité persistée "Dynamic_Class"

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

		// ************************
		// Creation de l'entité persistée "Adresse"

		RootClass pClass2 = new RootClass(metadataBuildingContext);
		pClass2.setEntityName("Adresse");
		pClass2.setJpaEntityName("Adresse");
		pClass2.setTable(table2);
		metadataCollector.addEntityBinding(pClass2);

		// Creation d'une propriété (clef) et son mapping

		prop = new Property();
		prop.setName("Identifiant");
		value = new SimpleValue((MetadataImplementor) metadata, table2);
		value.setTypeName("java.lang.Integer");
		value.setIdentifierGeneratorStrategy("native");
		value.addColumn(col4);
		value.setTable(table2);
		prop.setValue(value);
		pClass2.setDeclaredIdentifierProperty(prop);
		pClass2.setIdentifierProperty(prop);
		pClass2.setIdentifier(value);

		// Creation d'une propriété et son mapping

		prop = new Property();
		prop.setName("Prenom");
		value = new SimpleValue((MetadataImplementor) metadata, table2);
		value.setTypeName(String.class.getCanonicalName());
		value.addColumn(col5);
		value.setTable(table2);
		prop.setValue(value);
		pClass2.addProperty(prop);

		try {
			((MetadataImplementor) metadata).validate();
		} catch (MappingException e) {
			System.out.println("Validation Error: " + e.getMessage());
		}

		Namespace namespace = metadataCollector.getDatabase().getDefaultNamespace();

		for (JDBCTable aTable : connection.getSchema().getTables()) {
			System.out.println("Found table:  " + aTable + " hop: " + aTable.getName());
			Identifier logicalName = metadataCollector.getDatabase().toIdentifier(aTable.getName());
			System.out.println("logicalName=" + logicalName);
			Table laTable = namespace.locateTable(logicalName);
			System.out.println("latable=" + laTable);
		}

	}

	/*@Test
	@TestOrder(6)
	public void testPopulateDB() {
		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		Session hbnSession = hbnSessionFactory.withOptions().openSession();
	
		Map<String, String> syl = new HashMap<>();
		syl.put("Nom", "Sylvain");
		Map<String, String> chris = new HashMap<>();
		chris.put("Nom", "Guychard");
		chris.put("Prenom", "Christophe");
		Map<String, String> poulout = new HashMap<>();
		poulout.put("Nom", "Poulout");
	
		// Sérialisation de l'instance
		// Hibernate native
		Transaction trans = hbnSession.beginTransaction();
	
		hbnSession.save("Dynamic_Class", syl);
		hbnSession.save("Dynamic_Class", chris);
		hbnSession.save("Dynamic_Class", poulout);
	
		trans.commit();
	}*/

	/*@Test
	@TestOrder(7)
	public void testPopulateDB2() {
		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		Session hbnSession = hbnSessionFactory.withOptions().openSession();
	
		Map<String, String> syl = new HashMap<>();
		syl.put("Nom", "Sylvain2");
		Map<String, String> chris = new HashMap<>();
		chris.put("Nom", "Guychard2");
		chris.put("Prenom", "Christophe2");
	
		// Sérialisation de l'instance
		// Hibernate native
		Transaction trans = hbnSession.beginTransaction();
	
		hbnSession.save("Dynamic_Class", syl);
		hbnSession.save("Dynamic_Class", chris);
	
		trans.commit();
	}*/

	@Test
	@TestOrder(8)
	public void testConnectToDB() {

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>  On essaie de faire un truc");

		MetadataBuildingContextRootImpl metadataBuildingContext = new MetadataBuildingContextRootImpl(buildingOptions,
				new ClassLoaderAccessImpl(null, config.getServiceRegistry()), metadataCollector);
		metadata = metadataCollector.buildMetadataInstance(metadataBuildingContext);

		SessionFactory hbnSessionFactory = metadata.buildSessionFactory();
		Session hbnSession = hbnSessionFactory.withOptions().openSession();

		Iterable<Namespace> namespaces = metadataCollector.getDatabase().getNamespaces();

		System.out.println("Prout debut");
		for (Namespace ns : namespaces) {
			System.out.println("> hop: " + ns);
		}
		System.out.println("Prout fin");

		Namespace namespace = metadataCollector.getDatabase().getDefaultNamespace();

		for (JDBCTable table : connection.getSchema().getTables()) {
			System.out.println("Found table:  " + table + " hop: " + table.getName());
			Identifier logicalName = metadataCollector.getDatabase().toIdentifier(table.getName());
			System.out.println("logicalName=" + logicalName);
			Table laTable = namespace.locateTable(logicalName);
			System.out.println("latable=" + laTable);
		}

		System.out.println(namespace.getTables());

		/*Map<String, String> syl = new HashMap<>();
		syl.put("Nom", "Sylvain2");
		Map<String, String> chris = new HashMap<>();
		chris.put("Nom", "Guychard2");
		chris.put("Prenom", "Christophe2");
		
		// Sérialisation de l'instance
		// Hibernate native
		Transaction trans = hbnSession.beginTransaction();
		
		hbnSession.save("Dynamic_Class", syl);
		hbnSession.save("Dynamic_Class", chris);
		
		trans.commit();*/

		NativeQuery<?> sqlQ = hbnSession.createNativeQuery("select * from T_Dynamic_Table_2;");
		List<?> result = sqlQ.getResultList();
		assertEquals(5, result.size());

		for (Object o : result) {
			System.out.println(" > " + o + " of " + o.getClass());
			if (o.getClass().isArray()) {
				Object[] array = (Object[]) o;
				for (Object o2 : array) {
					System.out.println("  >> " + o2);
				}
			}
		}

		System.out.println("DEBUT");
		Set<EntityType<?>> entities = hbnSession.getMetamodel().getEntities();
		for (EntityType<?> ent : entities) {
			System.out.println("Entité dynamique: " + ent.getName());
		}
		System.out.println("FIN");

		System.err.println(hbnSession.load("Dynamic_Class", "Sylvain"));

		hbnSession.close();
	}

}
