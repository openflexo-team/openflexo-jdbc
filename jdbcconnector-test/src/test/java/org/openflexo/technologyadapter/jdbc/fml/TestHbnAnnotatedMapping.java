/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

package org.openflexo.technologyadapter.jdbc.fml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.PrimitiveRole;
import org.openflexo.foundation.fml.PropertyCardinality;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AddUseDeclaration;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateFlexoConceptInstanceRole;
import org.openflexo.foundation.fml.action.CreateGenericBehaviourParameter;
import org.openflexo.foundation.fml.action.CreateModelSlot;
import org.openflexo.foundation.fml.action.CreatePrimitiveRole;
import org.openflexo.foundation.fml.editionaction.ConnectAction;
import org.openflexo.foundation.fml.md.MultiValuedMetaData;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.jdbc.test.HsqlTestCase;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.dbtype.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCConnection;
import org.openflexo.technologyadapter.jdbc.hbn.JDBCMetaData;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnFlexoConceptInstance;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Greenfield test proving the annotation-driven mapping mechanism of {@link HbnModelSlot}.
 *
 * <p>
 * Unlike the (legacy, now @Ignore'd) {@code TestJDBCVirtualModel*} tests, this test:
 * </p>
 * <ul>
 * <li>hand-builds an <b>annotated</b> mapping {@link VirtualModel} (plain {@link PrimitiveRole} / {@link FlexoConceptInstanceRole}
 * properties carrying {@code @Table} / {@code @Property} FML meta-data), instead of generating role-based mappings through
 * {@code CreateJDBCVirtualModel};</li>
 * <li>connects the model slot through {@link ConnectAction} ({@code connect db using connection}) which calls
 * {@link HbnModelSlot#connectTo}, instead of the removed {@code CreateHbnResource} flow;</li>
 * <li>produces {@link HbnFlexoConceptInstance}s through a query (the reflection is query-driven, not exhaustive).</li>
 * </ul>
 *
 * <p>
 * The mapped schema is a simple many-to-one / one-to-many: {@code CLIENT(ID, NAME, SALESMAN)} references {@code SALESMAN(ID, LASTNAME)}.
 * </p>
 */
@RunWith(OrderedRunner.class)
public class TestHbnAnnotatedMapping extends HsqlTestCase {

	private static final String ROOT_VIRTUAL_MODEL_NAME = "RootVirtualModel";
	private static final String ROOT_VIRTUAL_MODEL_URI = "http://openflexo.org/test/" + ROOT_VIRTUAL_MODEL_NAME + ".fml";
	private static final String MAPPING_VIRTUAL_MODEL_NAME = "MappingVirtualModel";
	private static final String MAPPING_VIRTUAL_MODEL_URI = "http://openflexo.org/test/" + MAPPING_VIRTUAL_MODEL_NAME + ".fml";
	private static final String PROJECT_NAME = "TestHbnAnnotatedMappingProject";
	private static final String VIRTUAL_MODEL_INSTANCE_NAME = "TestDB";

	private static JDBCTable clientTable;
	private static JDBCTable salesmanTable;
	private static JDBCConnection connection;

	private static VirtualModel rootVirtualModel;
	private static VirtualModel mappingVirtualModel;
	private static FlexoConcept clientConcept;
	private static FlexoConcept salesmanConcept;

	private static HbnModelSlot modelSlot;
	private static CreationScheme creationScheme;

	private static FMLRTVirtualModelInstance vmi;
	private static HbnVirtualModelInstance dbVMI;

	@AfterClass
	public static void tearDownClass() {
		if (clientTable != null) {
			dropTable(connection, clientTable);
		}
		if (salesmanTable != null) {
			dropTable(connection, salesmanTable);
		}
		try {
			connection.getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@TestOrder(1)
	public void initializeDatabaseStructure() throws Exception {
		log("initializeDatabaseStructure");

		connection = createHSQLMemoryConnection("annotatedDb");

		clientTable = createTable(connection, "CLIENT", createPrimaryKeyIntegerAttribute("ID"), createStringAttribute("NAME", 256),
				createForeignKeyIntegerAttribute("SALESMAN"));
		salesmanTable = createTable(connection, "SALESMAN", createPrimaryKeyIntegerAttribute("ID"), createStringAttribute("LASTNAME", 256));

		assertEquals(2, connection.getSchema().getTables().size());
		clientTable = connection.getSchema().getTables().get(0);
		salesmanTable = connection.getSchema().getTables().get(1);
		assertEquals(3, clientTable.getColumns().size());
		assertEquals(2, salesmanTable.getColumns().size());
	}

	@Test
	@TestOrder(2)
	public void populateDatabase() throws Exception {
		log("populateDatabase");

		assertNotNull(salesmanTable.insert(new String[] { "ID", "1", "LASTNAME", "Smith" }));
		assertNotNull(salesmanTable.insert(new String[] { "ID", "2", "LASTNAME", "Rowland" }));

		assertNotNull(clientTable.insert(new String[] { "ID", "1", "NAME", "Préseau", "SALESMAN", "1" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "2", "NAME", "Carrington", "SALESMAN", "2" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "3", "NAME", "Barnowsky", "SALESMAN", "1" }));
	}

	@Test
	@TestOrder(3)
	public void createProject() throws Exception {
		log("createProject");
		_editor = createStandaloneProject(PROJECT_NAME);
	}

	/**
	 * Hand-build the annotated mapping VirtualModel: two concepts carrying {@code @Table} / {@code @Property} FML meta-data.
	 */
	@Test
	@TestOrder(4)
	public void createAnnotatedMappingVirtualModel() throws Exception {
		log("createAnnotatedMappingVirtualModel");

		mappingVirtualModel = createTopLevelVirtualModel(_project, MAPPING_VIRTUAL_MODEL_NAME, MAPPING_VIRTUAL_MODEL_URI);

		// concept Salesman @Table("SALESMAN") { key int id @Property(column="ID",id); String lastname @Property(column="LASTNAME");
		// Client[0,*] clients @Property(mappedBy="SALESMAN"); }
		salesmanConcept = createConcept(mappingVirtualModel, "Salesman");
		setTable(salesmanConcept, "SALESMAN");
		PrimitiveRole<?> salesmanId = createPrimitiveRole(salesmanConcept, "id", PrimitiveType.Integer);
		setColumn(salesmanId, "ID", true);
		salesmanConcept.addToKeyProperties(salesmanId);
		setColumn(createPrimitiveRole(salesmanConcept, "lastname", PrimitiveType.String), "LASTNAME", false);

		// concept Client @Table("CLIENT") { key int id @Property(column="ID",id); String name @Property(column="NAME"); Salesman salesman
		// @Property(column="SALESMAN",fk="ID"); }
		clientConcept = createConcept(mappingVirtualModel, "Client");
		setTable(clientConcept, "CLIENT");
		PrimitiveRole<?> clientId = createPrimitiveRole(clientConcept, "id", PrimitiveType.Integer);
		setColumn(clientId, "ID", true);
		clientConcept.addToKeyProperties(clientId);
		setColumn(createPrimitiveRole(clientConcept, "name", PrimitiveType.String), "NAME", false);

		// References (created once both concepts exist)
		FlexoConceptInstanceRole salesmanRef = createConceptRole(clientConcept, "salesman", salesmanConcept, PropertyCardinality.One);
		setToOneReference(salesmanRef, "SALESMAN", "ID");

		FlexoConceptInstanceRole clientsRef = createConceptRole(salesmanConcept, "clients", clientConcept, PropertyCardinality.ZeroMany);
		setToManyReference(clientsRef, "SALESMAN");

		mappingVirtualModel.getResource().save();

		assertNotNull(clientConcept.getAccessibleProperty("id"));
		assertNotNull(clientConcept.getAccessibleProperty("salesman"));
		assertEquals("CLIENT", JDBCMetaData.getTableName(clientConcept));
		assertEquals("NAME", JDBCMetaData.getColumnName(clientConcept.getAccessibleProperty("name")));
		assertEquals("ID", JDBCMetaData.getForeignKeyAttribute(clientConcept.getAccessibleProperty("salesman")));
		assertEquals("SALESMAN", JDBCMetaData.getMappedBy(salesmanConcept.getAccessibleProperty("clients")));

		System.err.println("FML: " + mappingVirtualModel.getFMLPrettyPrint());
	}

	/**
	 * Build the root VirtualModel: a {@link HbnModelSlot} typed with the annotated mapping VM, and a creation scheme that opens a JDBC
	 * connection and connects the model slot to it (query-driven reflection, no exhaustive population).
	 */
	@Test
	@TestOrder(5)
	public void createRootVirtualModelWithModelSlot() throws Exception {
		log("createRootVirtualModelWithModelSlot");

		rootVirtualModel = createTopLevelVirtualModel(_project, ROOT_VIRTUAL_MODEL_NAME, ROOT_VIRTUAL_MODEL_URI);

		AddUseDeclaration useDeclaration = AddUseDeclaration.actionType.makeNewAction(rootVirtualModel.getCompilationUnit(), null, _editor);
		useDeclaration.setModelSlotClass(HbnModelSlot.class);
		useDeclaration.doAction();

		CreateModelSlot createMS = CreateModelSlot.actionType.makeNewAction(rootVirtualModel, null, _editor);
		createMS.setTechnologyAdapter(getTA(JDBCTechnologyAdapter.class));
		createMS.setModelSlotClass(HbnModelSlot.class);
		createMS.setModelSlotName("db");
		createMS.setVmRes(mappingVirtualModel.getResource());
		createMS.doAction();
		assertTrue(createMS.hasActionExecutionSucceeded());
		modelSlot = (HbnModelSlot) createMS.getNewModelSlot();
		assertNotNull(modelSlot);
		assertSame(mappingVirtualModel, modelSlot.getAccessedVirtualModel());

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(rootVirtualModel, null, _editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.doAction();
		creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		createStringParameter(creationScheme, "address");
		createStringParameter(creationScheme, "user");
		createStringParameter(creationScheme, "password");

		// connection = new JDBCConnection(...)
		CreateEditionAction createConnectionAction = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				_editor);
		createConnectionAction.setEditionActionClass(CreateJDBCConnection.class);
		createConnectionAction.setDeclarationVariableName("connection");
		createConnectionAction.doAction();
		CreateJDBCConnection createJDBCConnectionAction = (CreateJDBCConnection) createConnectionAction.getBaseEditionAction();
		createJDBCConnectionAction.setAddress(new DataBinding<String>("parameters.address"));
		createJDBCConnectionAction.setUser(new DataBinding<String>("parameters.user"));
		createJDBCConnectionAction.setPassword(new DataBinding<String>("parameters.password"));
		createJDBCConnectionAction.setDbType(JDBCDbType.HSQLDB);
		createJDBCConnectionAction.setResourceName(new DataBinding<String>("(this.name + \"_connection\")"));
		createJDBCConnectionAction.setResourceCenter(new DataBinding<FlexoResourceCenter<?>>("this.resourceCenter"));

		// connect db using connection
		CreateEditionAction createConnectAction = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				_editor);
		createConnectAction.setEditionActionClass(ConnectAction.class);
		createConnectAction.doAction();
		ConnectAction<?, ?> connectAction = (ConnectAction<?, ?>) createConnectAction.getBaseEditionAction();
		connectAction.setConnect(new DataBinding<>("db"));
		// 'using' expects the FlexoResource (JDBCResource), not the JDBCConnection resource data
		connectAction.setUsing(new DataBinding<>("connection.resource"));

		rootVirtualModel.getResource().save();

		assertVirtualModelIsValid(rootVirtualModel);
	}

	@Test
	@TestOrder(6)
	public void instantiateAndConnect() throws Exception {
		log("instantiateAndConnect");

		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(_project.getVirtualModelInstanceRepository().getRootFolder(), null, _editor);
		action.setNewVirtualModelInstanceName(VIRTUAL_MODEL_INSTANCE_NAME);
		action.setVirtualModel(rootVirtualModel);
		action.setCreationScheme(creationScheme);
		action.setParameterValue(creationScheme.getParameter("address"), "jdbc:hsqldb:mem:annotatedDb");
		action.setParameterValue(creationScheme.getParameter("user"), "SA");
		action.setParameterValue(creationScheme.getParameter("password"), "");
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		vmi = action.getNewVirtualModelInstance();

		dbVMI = vmi.execute("db");
		assertNotNull(dbVMI);
		// Query-driven: no FlexoConceptInstance created at connect time
		assertTrue(dbVMI.getFlexoConceptInstances().isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	@TestOrder(7)
	public void queryAndNavigate() throws Exception {
		log("queryAndNavigate");

		// Query drives reflection: one HbnFlexoConceptInstance per row
		List<HbnFlexoConceptInstance> clients = dbVMI
				.getFlexoConceptInstances(dbVMI.getDefaultSession().createQuery("select o from Client o"), null, clientConcept);
		assertEquals(3, clients.size());

		HbnFlexoConceptInstance client1 = clients.get(0);
		HbnFlexoConceptInstance client2 = clients.get(1);
		HbnFlexoConceptInstance client3 = clients.get(2);

		// Scalar column read (@Property(column=...))
		assertEquals(1, (long) (Integer) client1.execute("id"));
		assertEquals("Préseau", client1.execute("name"));
		assertEquals("Carrington", client2.execute("name"));

		// To-one navigation (@Property(column=..., fk=...))
		HbnFlexoConceptInstance salesman1 = client1.execute("salesman");
		assertNotNull(salesman1);
		assertEquals("Smith", salesman1.execute("lastname"));
		assertSame(salesman1, client3.execute("salesman"));
		assertEquals("Rowland", ((HbnFlexoConceptInstance) client2.execute("salesman")).execute("lastname"));

		// One-to-many navigation (@Property(mappedBy=...)) : salesman 1 has clients 1 and 3
		List<HbnFlexoConceptInstance> salesman1Clients = (List<HbnFlexoConceptInstance>) salesman1.execute("clients");
		assertEquals(2, salesman1Clients.size());
	}

	@Test
	@TestOrder(8)
	public void selectTriggersQuery() throws Exception {
		log("selectTriggersQuery");

		// An FML "select Salesman from db" resolves to HbnVirtualModelInstance.getFlexoConceptInstances(salesmanConcept), which must
		// transparently perform the SQL query (query-driven reflection) and reflect the rows as HbnFlexoConceptInstances.
		assertEquals(2, dbVMI.getFlexoConceptInstances(salesmanConcept).size());
		// Calling again reuses the already-reflected instances (no re-query)
		assertEquals(2, dbVMI.getFlexoConceptInstances(salesmanConcept).size());
	}

	// ----------------------------------------------------------------------------------------------------------------------------------
	// Helpers building the annotated mapping VirtualModel
	// ----------------------------------------------------------------------------------------------------------------------------------

	private FlexoConcept createConcept(VirtualModel vm, String name) {
		CreateFlexoConcept action = CreateFlexoConcept.actionType.makeNewAction(vm, null, _editor);
		action.setNewFlexoConceptName(name);
		action.doAction();
		return action.getNewFlexoConcept();
	}

	private PrimitiveRole<?> createPrimitiveRole(FlexoConcept concept, String name, PrimitiveType type) {
		CreatePrimitiveRole action = CreatePrimitiveRole.actionType.makeNewAction(concept, null, _editor);
		action.setRoleName(name);
		action.setPrimitiveType(type);
		action.doAction();
		return action.getNewFlexoRole();
	}

	private FlexoConceptInstanceRole createConceptRole(FlexoConcept concept, String name, FlexoConcept type, PropertyCardinality card) {
		CreateFlexoConceptInstanceRole action = CreateFlexoConceptInstanceRole.actionType.makeNewAction(concept, null, _editor);
		action.setRoleName(name);
		action.setFlexoConceptInstanceType(type);
		action.setCardinality(card);
		action.doAction();
		return action.getNewFlexoRole();
	}

	private void setTable(FlexoConcept concept, String tableName) {
		concept.setSingleMetaData(JDBCMetaData.TABLE, tableName, String.class);
	}

	private void setColumn(FlexoProperty<?> property, String columnName, boolean isKey) {
		MultiValuedMetaData md = property.getFMLModelFactory().newMultiValuedMetaData(JDBCMetaData.PROPERTY);
		md.setValue(JDBCMetaData.COLUMN, columnName, String.class);
		if (isKey) {
			md.setValue(JDBCMetaData.ID, "true", String.class);
		}
		property.addToMetaData(md);
	}

	private void setToOneReference(FlexoProperty<?> property, String columnName, String foreignKeyAttribute) {
		MultiValuedMetaData md = property.getFMLModelFactory().newMultiValuedMetaData(JDBCMetaData.PROPERTY);
		md.setValue(JDBCMetaData.COLUMN, columnName, String.class);
		md.setValue(JDBCMetaData.FK, foreignKeyAttribute, String.class);
		property.addToMetaData(md);
	}

	private void setToManyReference(FlexoProperty<?> property, String destinationKeyColumn) {
		MultiValuedMetaData md = property.getFMLModelFactory().newMultiValuedMetaData(JDBCMetaData.PROPERTY);
		md.setValue(JDBCMetaData.MAPPED_BY, destinationKeyColumn, String.class);
		property.addToMetaData(md);
	}

	private void createStringParameter(CreationScheme cs, String name) {
		CreateGenericBehaviourParameter action = CreateGenericBehaviourParameter.actionType.makeNewAction(cs, null, _editor);
		action.setParameterName(name);
		action.setParameterType(String.class);
		action.doAction();
		assertNotNull(action.getNewParameter());
	}
}
