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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test JDBC Resource Creation
 *
 * @author xtof
 *
 */
@RunWith(OrderedRunner.class)
public class TestJDBCResource extends OpenflexoProjectAtRunTimeTestCase {

	protected final static String jdbcURL = "jdbc:hsqldb:data/hbnTests";
	protected final static String jdbcWrongURL = "jdbc:doesnotexist:data/hbnTests";
	protected final static String jdbcJarURL = "jdbc:mariadb://localhost:3306/DB";
	protected final static String jdbcDriverClassname = "org.hsqldb.jdbcDriver";
	protected final static String jdbcWrongDriverClassname = "org.doesnotexist.jdbcDriver";
	protected final static String jdbcJarDriverClassname = "org.mariadb.jdbc.Driver";
	protected final static String jdbcUser = "sa";
	protected final static String jdbcPwd = "";

	static private JDBCResourceFactory factory;

	private static FlexoProject project;
	private static FlexoEditor editor;
	private static JDBCTechnologyAdapter jdbcTA;
	private static RepositoryFolder<FMLRTVirtualModelInstanceResource, ?> viewFolder;

	private static String resourceName = "testing.jdbc";
	private static String resourceURI = "http://www.openflexo.org/testing.jdbc";

	private static JDBCResource resource;

	@Test
	@TestOrder(1)
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager(FMLTechnologyAdapter.class, JDBCTechnologyAdapter.class);

	}

	@Test
	@TestOrder(2)
	public void createsProjectAndFactories() throws Exception {

		log("createsProjectAndFactories()");

		jdbcTA = getFlexoServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
		factory = jdbcTA.getResourceFactory(JDBCResourceFactory.class);

		editor = createProject("TestJDBCResource");
		project = editor.getProject();

		assertNotNull(project);

	}


	@Test
	@TestOrder(3)
	public void createResource() throws Exception {
		log("createResource()");
		
		assertNotNull(project);
		
		Object sa = null;
		RepositoryFolder folder = project.getRootFolder();
		resource = factory.makeJDBCResource(resourceName, folder,
				(TechnologyContextManager<JDBCTechnologyAdapter>) jdbcTA.getTechnologyContextManager());

		JDBCConnection model = resource.getResourceData(null);

		assertNotNull(model);

		model.setDbType(JDBCDbType.GENERIC);
		model.setAddress(jdbcURL);
		model.setDriverClassName(jdbcDriverClassname);
		
		resource.save(null);
	}

	@Test
	@TestOrder(4)
	public void tryConnection() throws Exception {
		log("tryConnection()");
		assertNotNull(resource);
		assertNotNull(resource.getResourceData(null));

		JDBCConnection model = resource.getResourceData(null);

		model.setDbType(JDBCDbType.HSQLDB);
		model.setAddress(jdbcURL);
		model.setDriverClassName(jdbcDriverClassname);

		Connection conn = model.getConnection();

		assertNotNull(conn);
		conn.close();
		conn = null;

		resource.save(null);
	}

	@Test
	@TestOrder(5)
	public void tryWrongURL() throws Exception {
		log("tryWrongURL()");
		assertNotNull(resource);

		JDBCConnection model = resource.getResourceData(null);

		assertNotNull(model);

		model.setAddress(jdbcWrongURL);

		Connection conn = model.getConnection();

		assertNull(conn);
		model.setAddress(jdbcURL);
	}

	@Test
	@TestOrder(6)
	public void tryWrongDriverClassName() throws Exception {
		log("tryWrongDriverClassName()");
		assertNotNull(resource);

		JDBCConnection model = resource.getResourceData(null);

		assertNotNull(model);

		model.setDriverClassName(jdbcWrongDriverClassname);

		model.setDbType(null);

		Connection conn = model.getConnection();

		assertNull(conn);
		model.setDriverClassName(jdbcDriverClassname);
		model.setDbType(JDBCDbType.HSQLDB);
	}

	@Test
	@TestOrder(7)
	public void tryLoadDriverFromJar() throws Exception {

		log("tryLoadDriverFromJar()");

		assertNotNull(resource);

		JDBCConnection model = resource.getResourceData(null);

		assertNotNull(model);

		String jdbcDriverJarname = this.getClass().getResource("/jars/mariadb-java-client-2.1.2.jar").getPath();

		model.setDriverClassName(jdbcJarDriverClassname);
		model.setDriverJarName(jdbcDriverJarname);
		model.setAddress(jdbcJarURL);
		model.setDbType(JDBCDbType.GENERIC);

		Connection conn = model.getConnection();

		if (conn == null) {
			Exception exc = model.getException();
			if (exc != null) {
				if (exc.getCause() instanceof SQLException | exc instanceof SQLNonTransientConnectionException) {
					log("Pas de connection, mais test OK:  " + exc.getMessage());
				}
				else {
					fail(exc.getMessage());
				}
			}
			else {
				fail("Pas de connection MAIS pas d'exception ");
			}
		}
		resource.save(null);
	}

}
