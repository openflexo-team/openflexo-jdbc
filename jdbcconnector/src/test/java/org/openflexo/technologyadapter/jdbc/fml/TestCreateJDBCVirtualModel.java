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
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.ViewPointRepository;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rm.ViewPointResourceFactory;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.ModelUtils;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModelAction;

/**
 *
 * Tests for {@link CreateJDBCVirtualModelAction}, testing
 * 
 */
public class TestCreateJDBCVirtualModel extends OpenflexoProjectAtRunTimeTestCase {

	private final String VIEWPOINT_NAME = "TestTestCreateJDBCVirtualModel";
	private final String VIEWPOINT_URI = "http://openflexo.org/test/TestCreateJDBCVirtualModel.viewpoint";

	private final String PROJECT_NAME = "CreateJDBCVirtualModelProject";
	private final String VIRTUAL_MODEL_NAME = "TestVirtualModel";


	@AfterClass
	public static void tearDownClass() {
		deleteTestResourceCenters();
		unloadServiceManager();
	}

	private FMLTechnologyAdapter getFMLTechnologyAdapter() {
		return serviceManager.getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
	}

	private ViewPoint createViewPoint(FlexoEditor project)
			throws org.openflexo.foundation.resource.SaveResourceException, ModelDefinitionException
	{
		FMLTechnologyAdapter fmlTechnologyAdapter = getFMLTechnologyAdapter();
		ViewPointResourceFactory factory = fmlTechnologyAdapter.getViewPointResourceFactory();
		ViewPointRepository<File> viewPointRepository = project.getProject().getViewPointRepository();
		ViewPointResource viewPointResource = factory.makeViewPointResource(
				VIEWPOINT_NAME, VIEWPOINT_URI,
				viewPointRepository.getRootFolder(),
				fmlTechnologyAdapter.getTechnologyContextManager(),
				true
		);
		return viewPointResource.getLoadedResourceData();
	}

	private JDBCConnection prepareDatabase() throws ModelDefinitionException {
		JDBCConnection connection = ModelUtils.createJDBCMemoryConnection(".");
		ModelUtils.createTable1("table1", connection.getSchema());
		ModelUtils.createTable2("table2", connection.getSchema());
		ModelUtils.createTable3("table3", connection.getSchema());
		return connection;
	}

	private void checkResult(VirtualModel virtualModel) {
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

	/**
	 * Tests with an empty database
	 */
	@Test
	public void testGenerateEmptyVirtualModel() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		ViewPoint viewPoint = createViewPoint(project);

		log("Generate VirtualModel");
		CreateJDBCVirtualModelAction action = CreateJDBCVirtualModelAction.actionType.makeNewAction(viewPoint, new Vector<>(), project);
		action.setVirtualModelName(VIRTUAL_MODEL_NAME);
		action.setAddress("jdbc:hsqldb:mem:.");
		action.doAction();
		VirtualModel virtualModel = action.getVirtualModel();

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
		ViewPoint viewPoint = createViewPoint(project);

		log("Prepare database");
		JDBCConnection connection = prepareDatabase();

		log("Generate VirtualModel");
		CreateJDBCVirtualModelAction action = CreateJDBCVirtualModelAction.actionType.makeNewAction(viewPoint, new Vector<>(), project);
		action.setVirtualModelName(VIRTUAL_MODEL_NAME);
		action.setAddress(connection.getAddress());
		action.doAction();
		VirtualModel virtualModel = action.getVirtualModel();

		checkResult(virtualModel);
	}


	/**
	 * Tests with a set of tables and a synchronization scheme
	 */
	@Test
	public void testGenerateVirtualModelWithSynchronisationScheme() throws Exception {
		log("Creating project " + PROJECT_NAME);
		FlexoEditor project = createProject(PROJECT_NAME);

		log("Creating viewPoint " + VIEWPOINT_NAME);
		ViewPoint viewPoint = createViewPoint(project);

		log("Prepare database");
		JDBCConnection connection = prepareDatabase();

		log("Generate VirtualModel");
		CreateJDBCVirtualModelAction action = CreateJDBCVirtualModelAction.actionType.makeNewAction(viewPoint, new Vector<>(), project);
		action.setVirtualModelName(VIRTUAL_MODEL_NAME);
		action.setAddress(connection.getAddress());
		action.setGenerateSynchronizationScheme(true);
		action.doAction();
		VirtualModel virtualModel = action.getVirtualModel();

		checkResult(virtualModel);
		Assert.assertEquals(2, virtualModel.getFlexoBehaviours().size());
	}

}
