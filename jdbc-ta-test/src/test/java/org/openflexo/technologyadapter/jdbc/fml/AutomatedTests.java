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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.cli.CommandInterpreter;
import org.openflexo.foundation.fml.cli.ParseException;
import org.openflexo.foundation.fml.cli.command.FMLCommandExecutionException;
import org.openflexo.foundation.fml.cli.command.FMLScript;
import org.openflexo.foundation.fml.cli.command.fml.FMLAssertException;
import org.openflexo.foundation.fml.cli.test.FMLScriptParserTestCase;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.rm.Resources;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.dbtype.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.dbtype.HSQLUtils;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;

/**
 * A parameterized suite executing every <code>.fmlscript</code> found under
 * <code>TestResourceCenter/AutomatedTests</code> of the JDBC test resource center.
 *
 * <p>
 * This is the JDBC counterpart of the {@code AutomatedTests} runners of the other technology adapters (xml, xlsx): it lets JDBC use cases be
 * described declaratively as FML-scripts (loading FML resources, instantiating VirtualModels, querying and asserting) rather than as Java
 * code.
 * </p>
 */
@RunWith(Parameterized.class)
public class AutomatedTests extends FMLScriptParserTestCase {

	@Parameterized.Parameters(name = "{1}")
	public static Collection<Object[]> generateData() {
		return Resources.getMatchingResource(ResourceLocator.locateResource("TestResourceCenter/AutomatedTests"), ".fmlscript");
	}

	private final Resource fmlResource;
	private FlexoEditor editor;
	private FMLScript script;
	private CommandInterpreter commandInterpreter;

	public AutomatedTests(Resource fmlResource, String name) throws ParseException, ModelDefinitionException, IOException {
		System.out.println("********* Launch FML-script " + fmlResource + " name=" + name);
		this.fmlResource = fmlResource;
		initServiceManager();
	}

	@Test
	public void checkScript() throws ModelDefinitionException, ParseException, IOException, FMLCommandExecutionException {
		System.out.println("Parse script " + fmlResource.getRelativePath());
		script = parseFMLScript(fmlResource, commandInterpreter);
		checkFMLScript(fmlResource.getRelativePath(), script);
		try {
			script.execute();
		} catch (FMLAssertException e) {
			fail(e.getMessage());
		}
	}

	public void initServiceManager() throws ParseException, ModelDefinitionException, IOException {
		instanciateTestServiceManager(JDBCTechnologyAdapter.class);
		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);
		try {
			seedDatabaseOnce();
			provisionConnectionResource();
		} catch (Exception e) {
			throw new RuntimeException("Could not provision JDBC test database / connection resource", e);
		}
		commandInterpreter = new CommandInterpreter(serviceManager, System.in, System.out, System.err, HOME_DIR);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------
	// Database provisioning (infrastructure only: the test *logic* lives in the .fmlscript files)
	//
	// A single in-memory HSQLDB is seeded once (its seed connection is kept open so the memory database survives for the whole JVM), and
	// a JDBCConnection resource pointing at it is registered under a stable URI so that the .fmlscript files can 'load' it and connect the
	// FMLJDBCModelSlot to it.
	// ----------------------------------------------------------------------------------------------------------------------------------

	private static final String DB_NAME = "jdbcFmlScriptDb";
	private static final String DB_ADDRESS = "jdbc:hsqldb:mem:" + DB_NAME;
	private static final String CONNECTION_URI = "http://openflexo.org/jdbc-test/TestDB.jdbc";

	private static boolean dbSeeded = false;
	// Kept open on purpose: closing the last connection would drop the in-memory database
	@SuppressWarnings("unused")
	private static JDBCConnection seedConnection;

	private static synchronized void seedDatabaseOnce() throws Exception {
		if (dbSeeded) {
			return;
		}
		seedConnection = HSQLUtils.createHSQLMemoryConnection(DB_NAME);
		JDBCSchema schema = seedConnection.getSchema();

		JDBCTable salesman = schema.createTable("SALESMAN", new String[] { "ID", "INT", "PRIMARY KEY", "NOT NULL", "IDENTITY" },
				new String[] { "LASTNAME", "VARCHAR(256)" });
		JDBCTable client = schema.createTable("CLIENT", new String[] { "ID", "INT", "PRIMARY KEY", "NOT NULL", "IDENTITY" },
				new String[] { "NAME", "VARCHAR(256)" }, new String[] { "SALESMAN", "INT", "NULL" });

		salesman.insert(new String[] { "ID", "1", "LASTNAME", "Smith" });
		salesman.insert(new String[] { "ID", "2", "LASTNAME", "Rowland" });
		client.insert(new String[] { "ID", "1", "NAME", "Préseau", "SALESMAN", "1" });
		client.insert(new String[] { "ID", "2", "NAME", "Carrington", "SALESMAN", "2" });
		client.insert(new String[] { "ID", "3", "NAME", "Barnowsky", "SALESMAN", "1" });

		dbSeeded = true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void provisionConnectionResource() throws Exception {
		if (serviceManager.getResourceManager().getResource(CONNECTION_URI) != null) {
			return;
		}
		JDBCTechnologyAdapter ta = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
		JDBCResourceFactory factory = ta.getResourceFactory(JDBCResourceFactory.class);
		DirectoryResourceCenter rc = makeNewDirectoryResourceCenter(serviceManager);
		RepositoryFolder folder = ta.getGlobalRepository(rc).getRootFolder();

		JDBCResource resource = factory.makeJDBCResource("TestDB", folder);
		resource.setURI(CONNECTION_URI);
		JDBCConnection connection = resource.getResourceData();
		connection.setDbType(JDBCDbType.HSQLDB);
		connection.setAddress(DB_ADDRESS);
		connection.setUser("SA");
		connection.setPassword("");
		resource.save();
	}
}
