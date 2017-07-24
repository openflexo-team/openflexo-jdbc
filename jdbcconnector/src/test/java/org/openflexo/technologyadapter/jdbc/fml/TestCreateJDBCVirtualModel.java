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

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelRepository;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstanceRepository;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResourceFactory;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.ModelUtils;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModelAction;

/**
 *
 * Tests for {@link CreateJDBCVirtualModelAction}, testing
 *
 */
public class TestCreateJDBCVirtualModel extends OpenflexoProjectAtRunTimeTestCase {

	private final String VIEWPOINT_NAME = "TestCreateJDBCVirtualModel";
	private final String VIEWPOINT_URI = "http://openflexo.org/test/" + VIEWPOINT_NAME + ".viewpoint";

	private final String VIEW_NAME = "testCreateJDBCVirtualModel";
	private final String VIEW_URI = "http://openflexo.org/test/" + VIEW_NAME + ".view";

	private final String PROJECT_NAME = "CreateJDBCVirtualModelProject";

	private final String VIRTUAL_MODEL_NAME = "TestVirtualModel";
	private final String VIRTUAL_MODEL_INSTANCE_NAME = "testVirtualModel";

	@AfterClass
	public static void tearDownClass() {
		deleteTestResourceCenters();
		unloadServiceManager();
	}

	private <T extends TechnologyAdapter> T getTA(Class<T> type) {
		return serviceManager.getTechnologyAdapterService().getTechnologyAdapter(type);
	}

	private VirtualModel createViewPoint(FlexoEditor project)
			throws org.openflexo.foundation.resource.SaveResourceException, ModelDefinitionException {
		FMLTechnologyAdapter fmlTechnologyAdapter = getTA(FMLTechnologyAdapter.class);
		VirtualModelResourceFactory factory = fmlTechnologyAdapter.getVirtualModelResourceFactory();
		VirtualModelRepository<File> viewPointRepository = project.getProject().getVirtualModelRepository();
		VirtualModelResource viewPointResource = factory.makeTopLevelVirtualModelResource(VIEWPOINT_NAME, VIEWPOINT_URI,
				viewPointRepository.getRootFolder(), fmlTechnologyAdapter.getTechnologyContextManager(), true);

		viewPointResource.save(null);
		return viewPointResource.getLoadedResourceData();
	}

	private JDBCConnection prepareDatabase(String name) throws ModelDefinitionException {
		JDBCConnection connection = ModelUtils.createJDBCMemoryConnection(name);
		JDBCTable table1 = ModelUtils.createTable1("table1", connection.getSchema());
		ModelUtils.addLinesForTable1(table1);
		ModelUtils.createTable2("table2", connection.getSchema());
		JDBCTable table3 = ModelUtils.createTable3("table3", connection.getSchema());
		ModelUtils.addLinesForTable3(table3);
		return connection;
	}

	private VirtualModel generateVirtualModel(FlexoEditor project, VirtualModel viewPoint, String address, boolean synchronizationScheme) {
		CreateJDBCVirtualModelAction action = CreateJDBCVirtualModelAction.actionType.makeNewAction(viewPoint, new Vector<>(), project);
		action.setVirtualModelName(VIRTUAL_MODEL_NAME);
		action.setAddress(address);
		action.setGenerateSynchronizationScheme(synchronizationScheme);
		action.doAction();
		return action.getVirtualModel();
	}

	private void checkVirtualModel(VirtualModel virtualModel) {
		Assert.assertNotNull(virtualModel);
		List<FlexoConcept> flexoConcepts = virtualModel.getFlexoConcepts();
		Assert.assertEquals(3, flexoConcepts.size());

		FlexoConcept table1Concept = flexoConcepts.get(0);
		Assert.assertEquals("TABLE1", table1Concept.getName());
		Assert.assertEquals(3, table1Concept.getDeclaredProperties().size());

		FlexoConcept table2Concept = flexoConcepts.get(1);
		Assert.assertEquals("TABLE2", table2Concept.getName());
		Assert.assertEquals(6, table2Concept.getDeclaredProperties().size());

		FlexoConcept table3Concept = flexoConcepts.get(2);
		Assert.assertEquals("TABLE3", table3Concept.getName());
		Assert.assertEquals(5, table3Concept.getDeclaredProperties().size());
	}

	private FMLRTVirtualModelInstance createView(FlexoEditor project, VirtualModel virtualModel)
			throws SaveResourceException, ModelDefinitionException {
		FMLRTTechnologyAdapter fmlRtTechnologyAdapter = getTA(FMLRTTechnologyAdapter.class);
		FMLRTVirtualModelInstanceResourceFactory factory = fmlRtTechnologyAdapter.getFMLRTVirtualModelInstanceResourceFactory();
		FMLRTVirtualModelInstanceRepository<?> viewLibrary = project.getProject().getVirtualModelInstanceRepository();

		FMLRTVirtualModelInstanceResource viewResource = factory.makeTopLevelFMLRTVirtualModelInstanceResource(VIEW_NAME, VIEW_URI,
				virtualModel.getVirtualModelResource(), viewLibrary.getRootFolder(), fmlRtTechnologyAdapter.getTechnologyContextManager(),
				true);

		return viewResource.getLoadedResourceData();
	}

	private FMLRTVirtualModelInstance createVirtualModelInstance(FlexoEditor project, FMLRTVirtualModelInstance containerVMI,
			VirtualModel virtualModel) throws SaveResourceException, ModelDefinitionException {
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType.makeNewAction(containerVMI, null, project);
		action.setNewVirtualModelInstanceName("data");
		action.setNewVirtualModelInstanceTitle("data");
		action.setVirtualModel(virtualModel);
		action.setCreationScheme(virtualModel.getCreationSchemes().get(0));
		action.doAction();

		return action.getNewVirtualModelInstance();
	}

	/**
	 * Tests with an empty database
	 */
	@Test
	public void testGenerateEmptyVirtualModel() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		VirtualModel viewPoint = createViewPoint(project);

		log("Generate VirtualModel");
		VirtualModel virtualModel = generateVirtualModel(project, viewPoint, "jdbc:hsqldb:mem:.", false);
		Assert.assertNotNull(virtualModel);
		Assert.assertEquals(0, virtualModel.getFlexoConcepts().size());
	}

	/**
	 * Tests with a set of tables and no synchronization scheme
	 */
	@Test
	public void testGenerateVirtualModelNoSynchronisationScheme() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		VirtualModel viewPoint = createViewPoint(project);

		log("Prepare database");
		JDBCConnection connection = prepareDatabase("noSync");

		log("Generate VirtualModel");
		VirtualModel virtualModel = generateVirtualModel(project, viewPoint, connection.getAddress(), false);

		checkVirtualModel(virtualModel);
	}

	/**
	 * Tests with a set of tables and a synchronization scheme
	 */
	@Test
	public void testGenerateVirtualModelWithSynchronisationScheme() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		VirtualModel viewPoint = createViewPoint(project);

		log("Prepare database");
		JDBCConnection connection = prepareDatabase("sync");

		log("Generate VirtualModel");
		VirtualModel virtualModel = generateVirtualModel(project, viewPoint, connection.getAddress(), true);
		checkVirtualModel(virtualModel);
		Assert.assertEquals(2, virtualModel.getFlexoBehaviours().size());
	}

	/**
	 * Tests with a set of tables and a synchronization scheme
	 */
	@Test
	public void testGenerateVirtualModelAndSynchronize() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		VirtualModel viewPoint = createViewPoint(project);

		log("Prepare database");
		JDBCConnection connection = prepareDatabase("syncAndView");

		log("Generate VirtualModel");
		VirtualModel virtualModel = generateVirtualModel(project, viewPoint, connection.getAddress(), true);
		checkVirtualModel(virtualModel);
		Assert.assertEquals(2, virtualModel.getFlexoBehaviours().size());

		log("Create view " + VIEW_NAME);
		FMLRTVirtualModelInstance view = createView(project, viewPoint);

		log("Create virtual model instance " + VIRTUAL_MODEL_INSTANCE_NAME);
		FMLRTVirtualModelInstance instance = createVirtualModelInstance(project, view, virtualModel);

		System.out.println("FML=" + virtualModel.getFMLRepresentation());

		Assert.assertEquals(14, instance.getFlexoConceptInstances().size());
	}

}