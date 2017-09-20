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
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.AbstractProperty;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.PropertyCardinality;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AddUseDeclaration;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConceptInstanceRole;
import org.openflexo.foundation.fml.action.CreateGenericBehaviourParameter;
import org.openflexo.foundation.fml.action.CreateModelSlot;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CreateHbnResource;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnInitializer;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.PerformSQLQuery;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnFlexoConceptInstance;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCDbType.DbType;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModel;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Testing {@link HbnModelSlot} in the context of manyToOne relationship
 * 
 * <ul>
 * <li>Create a database and populate it</li>
 * <li>Create a {@link VirtualModel} mapping this database</li>
 * <li>Create a {@link VirtualModel} whose purpose is to declare and use a {@link HbnModelSlot} configured with mapping
 * {@link VirtualModel}</li>
 * <li>Instantiate this {@link VirtualModel} and perform some tests</li>
 * </ul>
 *
 */
@RunWith(OrderedRunner.class)
public class TestJDBCVirtualModelManyToOne extends JDBCTestCase {

	private final String ROOT_VIRTUAL_MODEL_NAME = "RootVirtualModel";
	private final String ROOT_VIRTUAL_MODEL_URI = "http://openflexo.org/test/" + ROOT_VIRTUAL_MODEL_NAME + ".fml";

	private final String PROJECT_NAME = "TestJDBCVirtualModelProject";

	private final String VIRTUAL_MODEL_NAME = "MappingVirtualModel";
	private final String VIRTUAL_MODEL_INSTANCE_NAME = "TestDB";

	private static JDBCTable clientTable;
	private static JDBCTable salesmanTable;

	private static VirtualModel rootVirtualModel;
	private static VirtualModel mappingVirtualModel;

	private static HbnModelSlot modelSlot;
	private static CreationScheme mappingCreationScheme;
	private static HbnInitializer mappingInitializer;
	private static FlexoConceptInstanceRole clientsProperty;
	private static CreationScheme creationScheme;

	private static FMLRTVirtualModelInstance vmi;
	private static HbnVirtualModelInstance dbVMI;

	@AfterClass
	public static void tearDownClass() {
		if (clientTable != null) {
			dropTable(clientTable);
		}
		if (salesmanTable != null) {
			dropTable(salesmanTable);
		}
	}
	@Test
	@TestOrder(1)
	public void initializeDatabaseStructure() throws Exception {
		log("initializeDatabase");

		createTable("CLIENT", createPrimaryKeyIntegerAttribute("ID"), createStringAttribute("NAME", 256),
				createStringAttribute("ADRESS", 512), createStringAttribute("HOBBY", 512), createStringAttribute("COMMENTS", 512),
				createDateAttribute("LASTMEETING"), createForeignKeyIntegerAttribute("SALESMAN"));

		createTable("SALESMAN", createPrimaryKeyIntegerAttribute("ID"), createStringAttribute("LASTNAME", 256),
				createStringAttribute("FIRSTNAME", 256));

		for (JDBCTable table : connection.getSchema().getTables()) {
			System.out.println("JDBCTable:  " + table);
			for (JDBCColumn column : table.getColumns()) {
				System.out.println(" > " + column.getName() + " " + column.getSQLType());
			}
		}

		assertEquals(2, connection.getSchema().getTables().size());

		clientTable = connection.getSchema().getTables().get(0);
		salesmanTable = connection.getSchema().getTables().get(1);

		assertEquals(7, clientTable.getColumns().size());
		assertEquals(3, salesmanTable.getColumns().size());

	}

	@Test
	@TestOrder(2)
	public void populateDatabase() throws Exception {
		log("populateDatabase");

		assertNotNull(clientTable.insert(new String[] { "ID", "1", "NAME", "Pr\u00e9seau", "ADRESS", "22 rue Solferino", "HOBBY",
				"Banana eater", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "1" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "2", "NAME", "Carrington", "ADRESS", "609 rue du 5ieme r\u00e9giment",
				"HOBBY", "Likes Cowboys", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "2" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "3", "NAME", "Barnowsky", "ADRESS", "548 rue Creeptown", "HOBBY",
				"Not cooking", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "3" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "4", "NAME", "Brown", "ADRESS", "34 rue Californie", "HOBBY", "Nursering",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "4" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "5", "NAME", "Giordino", "ADRESS", "1234 rue Harvard", "HOBBY",
				"Intelligence", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "1" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "6", "NAME", "Svetlanova", "ADRESS", "16 rue de Minsk", "HOBBY",
				"Poisons and cooking", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "2" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "7", "NAME", "Martin", "ADRESS", "18 rue Giordino", "HOBBY", "Keyboards",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "3" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "8", "NAME", "Jones", "ADRESS", "374 rue Watts", "HOBBY",
				"Piloting Helicopters", "COMMENTS", "", "LASTMEETING", null, "SALESMAN", "4" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "9", "NAME", "Mangouste", "ADRESS", "12 rue Greenfalls", "HOBBY", "Hunting",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "1" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "10", "NAME", "Los Santos", "ADRESS", "56 rue Jacinto", "HOBBY", "Parkour",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "2" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "11", "NAME", "Amos", "ADRESS", "1927 rue Irgoun", "HOBBY", "Conspirations",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "3" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "12", "NAME", "Mullway", "ADRESS", "7 rue Verde", "HOBBY", "Jungle",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "4" }));
		assertNotNull(clientTable.insert(new String[] { "ID", "13", "NAME", "Sheridan", "ADRESS", "22 rue Wally", "HOBBY", "Leadership",
				"COMMENTS", "", "LASTMEETING", null, "SALESMAN", "1" }));

		assertNotNull(salesmanTable.insert(new String[] { "ID", "1", "LASTNAME", "Smith", "FIRSTNAME", "Alan" }));
		assertNotNull(salesmanTable.insert(new String[] { "ID", "2", "LASTNAME", "Rowland", "FIRSTNAME", "Steve" }));
		assertNotNull(salesmanTable.insert(new String[] { "ID", "3", "LASTNAME", "Mac Lane", "FIRSTNAME", "Jason" }));
		assertNotNull(salesmanTable.insert(new String[] { "ID", "4", "LASTNAME", "Brian", "FIRSTNAME", "Kelly" }));
	}

	@Test
	@TestOrder(3)
	public void createProject() throws Exception {
		log("createProject");
		_editor = createProject(PROJECT_NAME);
	}

	@Test
	@TestOrder(4)
	public void createRootVirtualModel() throws Exception {
		log("createRootVirtualModel");
		rootVirtualModel = createTopLevelVirtualModel(_project, ROOT_VIRTUAL_MODEL_NAME, ROOT_VIRTUAL_MODEL_URI);
	}

	@Test
	@TestOrder(5)
	public void generateMappingVirtualModel() throws Exception {
		log("generateMappingVirtualModel");

		CreateJDBCVirtualModel action = CreateJDBCVirtualModel.actionType.makeNewAction(rootVirtualModel, null, _editor);
		action.setNewVirtualModelName(VIRTUAL_MODEL_NAME);
		action.setAddress(connection.getAddress());
		action.setUser(connection.getUser());
		action.setPassword(connection.getPassword());
		action.getTablesToBeReflected().add(clientTable);
		action.getTablesToBeReflected().add(salesmanTable);
		action.doAction();
		mappingVirtualModel = action.getNewVirtualModel();

		AddUseDeclaration useDeclarationAction = AddUseDeclaration.actionType.makeNewAction(mappingVirtualModel, null, _editor);
		useDeclarationAction.setModelSlotClass(HbnModelSlot.class);
		useDeclarationAction.doAction();

		assertNotNull(mappingVirtualModel);

		assertEquals(2, mappingVirtualModel.getFlexoConcepts().size());
		FlexoConcept clientConcept = mappingVirtualModel.getFlexoConcept("Client");
		FlexoConcept salesmanConcept = mappingVirtualModel.getFlexoConcept("Salesman");
		assertNotNull(clientConcept);
		assertNotNull(salesmanConcept);

		AbstractProperty<Integer> clientId = (AbstractProperty<Integer>) clientConcept.getAccessibleProperty("id");
		assertNotNull(clientId);
		AbstractProperty<String> name = (AbstractProperty<String>) clientConcept.getAccessibleProperty("name");
		assertNotNull(name);
		AbstractProperty<String> adress = (AbstractProperty<String>) clientConcept.getAccessibleProperty("adress");
		assertNotNull(adress);
		AbstractProperty<String> hobby = (AbstractProperty<String>) clientConcept.getAccessibleProperty("hobby");
		assertNotNull(hobby);
		AbstractProperty<String> comments = (AbstractProperty<String>) clientConcept.getAccessibleProperty("comments");
		assertNotNull(comments);
		AbstractProperty<Date> lastMeeting = (AbstractProperty<Date>) clientConcept.getAccessibleProperty("lastmeeting");
		assertNotNull(lastMeeting);
		HbnToOneReferenceRole salesman = (HbnToOneReferenceRole) clientConcept.getAccessibleProperty("salesman");
		assertNotNull(salesman);

		assertEquals("container", salesman.getVirtualModelInstance().toString());
		assertEquals(salesmanConcept, salesman.getFlexoConceptType());

		AbstractProperty<Integer> salesmanId = (AbstractProperty<Integer>) salesmanConcept.getAccessibleProperty("id");
		assertNotNull(salesmanId);
		AbstractProperty<String> lastname = (AbstractProperty<String>) salesmanConcept.getAccessibleProperty("lastname");
		assertNotNull(lastname);
		AbstractProperty<String> firstname = (AbstractProperty<String>) salesmanConcept.getAccessibleProperty("firstname");
		assertNotNull(firstname);

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(mappingVirtualModel, null, _editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.doAction();
		mappingCreationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateEditionAction createSelectClients1 = CreateEditionAction.actionType.makeNewAction(mappingCreationScheme.getControlGraph(),
				null, _editor);
		createSelectClients1.setEditionActionClass(PerformSQLQuery.class);
		createSelectClients1.setAssignation(new DataBinding<Object>("clients"));
		createSelectClients1.doAction();
		AssignationAction<?> assignation1 = (AssignationAction<?>) createSelectClients1.getNewEditionAction();
		PerformSQLQuery sqlQuery1 = (PerformSQLQuery) assignation1.getAssignableAction();
		sqlQuery1.setReceiver(new DataBinding<>("this"));
		sqlQuery1.setFlexoConceptType(clientConcept);

		CreateFlexoBehaviour createInitializer = CreateFlexoBehaviour.actionType.makeNewAction(mappingVirtualModel, null, _editor);
		createInitializer.setFlexoBehaviourClass(HbnInitializer.class);
		createInitializer.doAction();
		mappingInitializer = (HbnInitializer) createInitializer.getNewFlexoBehaviour();

		CreateEditionAction createSelectClients2 = CreateEditionAction.actionType.makeNewAction(mappingInitializer.getControlGraph(), null,
				_editor);
		createSelectClients2.setEditionActionClass(PerformSQLQuery.class);
		createSelectClients2.setAssignation(new DataBinding<Object>("clients"));
		createSelectClients2.doAction();
		AssignationAction<?> assignation2 = (AssignationAction<?>) createSelectClients2.getNewEditionAction();
		PerformSQLQuery sqlQuery2 = (PerformSQLQuery) assignation2.getAssignableAction();
		sqlQuery2.setReceiver(new DataBinding<>("this"));
		sqlQuery2.setFlexoConceptType(clientConcept);

		CreateFlexoConceptInstanceRole createClientsProperty = CreateFlexoConceptInstanceRole.actionType.makeNewAction(mappingVirtualModel,
				null, _editor);
		createClientsProperty.setRoleName("clients");
		createClientsProperty.setVirtualModelInstance(new DataBinding<>("this"));
		createClientsProperty.setFlexoConceptInstanceType(clientConcept);
		createClientsProperty.setCardinality(PropertyCardinality.ZeroMany);
		createClientsProperty.doAction();
		assertTrue(createClientsProperty.hasActionExecutionSucceeded());
		assertNotNull(clientsProperty = createClientsProperty.getNewFlexoRole());
		assertNotNull(clientsProperty);

		mappingVirtualModel.getResource().save(null);

		System.out.println(mappingVirtualModel.getFMLRepresentation());

		assertVirtualModelIsValid(mappingVirtualModel);

	}

	@Test
	@TestOrder(6)
	public void createConnexionInMetaModel() throws Exception {

		log("createConnexionInMetaModel");

		// Now we create the vm1 model slot
		CreateModelSlot createMS1 = CreateModelSlot.actionType.makeNewAction(rootVirtualModel, null, _editor);
		createMS1.setTechnologyAdapter(getTA(JDBCTechnologyAdapter.class));
		createMS1.setModelSlotClass(HbnModelSlot.class);
		createMS1.setModelSlotName("db");
		createMS1.setVmRes((VirtualModelResource) mappingVirtualModel.getResource());
		createMS1.doAction();
		assertTrue(createMS1.hasActionExecutionSucceeded());

		assertNotNull(modelSlot = (HbnModelSlot) createMS1.getNewModelSlot());
		System.out.println("Created " + modelSlot);

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(rootVirtualModel, null, _editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.doAction();
		creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateGenericBehaviourParameter createParameter1 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				_editor);
		createParameter1.setParameterName("address");
		createParameter1.setParameterType(String.class);
		createParameter1.doAction();
		FlexoBehaviourParameter adressParam = createParameter1.getNewParameter();
		assertNotNull(adressParam);

		CreateGenericBehaviourParameter createParameter2 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				_editor);
		createParameter2.setParameterName("user");
		createParameter2.setParameterType(String.class);
		createParameter2.doAction();
		FlexoBehaviourParameter userParam = createParameter2.getNewParameter();
		assertNotNull(userParam);

		CreateGenericBehaviourParameter createParameter3 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				_editor);
		createParameter3.setParameterName("password");
		createParameter3.setParameterType(String.class);
		createParameter3.doAction();
		FlexoBehaviourParameter passwordParam = createParameter3.getNewParameter();
		assertNotNull(passwordParam);

		CreateGenericBehaviourParameter createParameter4 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				_editor);
		createParameter4.setParameterName("dbtype");
		createParameter4.setParameterType(DbType.class);
		createParameter4.doAction();
		FlexoBehaviourParameter dbTypeParam = createParameter4.getNewParameter();
		assertNotNull(dbTypeParam);

		CreateEditionAction createEditionAction1 = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				_editor);
		createEditionAction1.setEditionActionClass(CreateHbnResource.class);
		createEditionAction1.setAssignation(new DataBinding<Object>("db"));
		createEditionAction1.doAction();
		AssignationAction<?> action1 = (AssignationAction<?>) createEditionAction1.getNewEditionAction();

		CreateHbnResource createHbnResourceAction = (CreateHbnResource) action1.getAssignableAction();
		createHbnResourceAction.setReceiver(new DataBinding<HbnVirtualModelInstance>("null"));
		createHbnResourceAction.setAddress(new DataBinding<String>("parameters.address"));
		createHbnResourceAction.setUser(new DataBinding<String>("parameters.user"));
		createHbnResourceAction.setPassword(new DataBinding<String>("parameters.password"));
		createHbnResourceAction.setDbType(new DataBinding<DbType>("parameters.dbtype"));
		createHbnResourceAction.setResourceName(new DataBinding<String>("(this.name + \"_db\")"));
		createHbnResourceAction.setResourceCenter(new DataBinding<FlexoResourceCenter<?>>("this.resourceCenter"));
		createHbnResourceAction.setCreationScheme(mappingCreationScheme);

		rootVirtualModel.getResource().save(null);

		System.out.println(rootVirtualModel.getFMLRepresentation());

		assertVirtualModelIsValid(rootVirtualModel);
	}

	@Test
	@TestOrder(7)
	public void instantiate() throws Exception {

		log("instantiate");

		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(_project.getVirtualModelInstanceRepository().getRootFolder(), null, _editor);
		action.setNewVirtualModelInstanceName(VIRTUAL_MODEL_INSTANCE_NAME);
		action.setVirtualModel(rootVirtualModel);
		action.setCreationScheme(creationScheme);
		action.setParameterValue(creationScheme.getParameter("address"), "jdbc:hsqldb:mem:db");
		action.setParameterValue(creationScheme.getParameter("user"), "SA");
		action.setParameterValue(creationScheme.getParameter("password"), "");
		action.setParameterValue(creationScheme.getParameter("dbtype"), DbType.HSQLDB);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		vmi = action.getNewVirtualModelInstance();

		dbVMI = vmi.execute("db");
		assertNotNull(dbVMI);

		assertEquals(13, dbVMI.getFlexoConceptInstances().size());
		HbnFlexoConceptInstance client1 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(0);
		HbnFlexoConceptInstance client2 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(1);
		HbnFlexoConceptInstance client3 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(2);
		HbnFlexoConceptInstance client4 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(3);
		HbnFlexoConceptInstance client5 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(4);
		HbnFlexoConceptInstance client6 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(5);
		HbnFlexoConceptInstance client7 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(6);
		HbnFlexoConceptInstance client8 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(7);
		HbnFlexoConceptInstance client9 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(8);
		HbnFlexoConceptInstance client10 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(9);
		HbnFlexoConceptInstance client11 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(10);
		HbnFlexoConceptInstance client12 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(11);
		HbnFlexoConceptInstance client13 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(12);

		assertEquals(1, (long) client1.execute("id"));

		assertEquals("Préseau", client1.execute("name"));
		assertEquals("22 rue Solferino", client1.execute("adress"));

		assertNotNull(client1.execute("salesman"));
		HbnFlexoConceptInstance salesman1 = client1.execute("salesman");
		assertNotNull(client2.execute("salesman"));
		HbnFlexoConceptInstance salesman2 = client2.execute("salesman");
		assertNotNull(client3.execute("salesman"));
		HbnFlexoConceptInstance salesman3 = client3.execute("salesman");
		assertNotNull(client4.execute("salesman"));
		HbnFlexoConceptInstance salesman4 = client4.execute("salesman");

		assertEquals(salesman1, client5.execute("salesman"));
		assertEquals(salesman2, client6.execute("salesman"));
		assertEquals(salesman3, client7.execute("salesman"));
		assertEquals(salesman4, client8.execute("salesman"));
		assertEquals(salesman1, client9.execute("salesman"));
		assertEquals(salesman2, client10.execute("salesman"));
		assertEquals(salesman3, client11.execute("salesman"));
		assertEquals(salesman4, client12.execute("salesman"));
		assertEquals(salesman1, client13.execute("salesman"));

	}

	@Test
	@TestOrder(8)
	public void modifySimpleData() throws Exception {

		log("modifySimpleData");

		Transaction transaction = dbVMI.getDefaultSession().beginTransaction();

		HbnFlexoConceptInstance client1 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(0);
		HbnFlexoConceptInstance client2 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(1);
		FlexoConcept clientConcept = client1.getFlexoConcept();

		client1.setFlexoPropertyValue((FlexoProperty<String>) clientConcept.getAccessibleProperty("name"), "another name");
		client2.setFlexoPropertyValue((FlexoProperty<String>) clientConcept.getAccessibleProperty("adress"), "another address");

		debugTable(dbVMI.getDefaultSession(), "Client");
		NativeQuery<?> sqlQ = dbVMI.getDefaultSession().createNativeQuery("select * from client;");

		// Still not changed in the database
		assertNativeQueryResult(sqlQ, 0, 1, "Préseau");
		assertNativeQueryResult(sqlQ, 1, 2, "609 rue du 5ieme régiment");

		System.out.println("C'est parti pour le commit");
		transaction.commit();

		// Should have been changed now
		debugTable(dbVMI.getDefaultSession(), "Client");
		NativeQuery<?> sqlQ2 = dbVMI.getDefaultSession().createNativeQuery("select * from client;");
		assertNativeQueryResult(sqlQ2, 0, 1, "another name");
		assertNativeQueryResult(sqlQ2, 1, 2, "another address");

	}

	@Test
	@TestOrder(9)
	public void modifyReferencedData() throws Exception {

		log("modifyReferencedData");

		Transaction transaction = dbVMI.getDefaultSession().beginTransaction();

		HbnFlexoConceptInstance client1 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(0);
		HbnFlexoConceptInstance client2 = (HbnFlexoConceptInstance) dbVMI.getFlexoConceptInstances().get(1);

		HbnFlexoConceptInstance salesman1 = client1.execute("salesman");
		HbnFlexoConceptInstance salesman2 = client2.execute("salesman");

		FlexoConcept clientConcept = client1.getFlexoConcept();

		client1.setFlexoPropertyValue((FlexoProperty<HbnFlexoConceptInstance>) clientConcept.getAccessibleProperty("salesman"), salesman2);

		assertSame(salesman2, client1.execute("salesman"));

		// Still not changed in the database
		debugTable(dbVMI.getDefaultSession(), "Client");
		NativeQuery<?> sqlQ = dbVMI.getDefaultSession().createNativeQuery("select * from client;");
		assertNativeQueryResult(sqlQ, 0, 6, 1);

		System.out.println("C'est parti pour le commit");
		transaction.commit();

		// Should have been changed now
		debugTable(dbVMI.getDefaultSession(), "Client");
		NativeQuery<?> sqlQ2 = dbVMI.getDefaultSession().createNativeQuery("select * from client;");
		assertNativeQueryResult(sqlQ, 0, 6, 2);

	}

	private static void assertNativeQueryResult(NativeQuery<?> query, int rowIndex, int colIndex, Object expectedObject) {
		List<?> resultList = query.getResultList();
		Object rowResult = resultList.get(rowIndex);
		if (rowResult.getClass().isArray()) {
			Object result = ((Object[]) rowResult)[colIndex];
			assertEquals(expectedObject, result);
		}
		else {
			fail("Not an array");
		}
	}

}
