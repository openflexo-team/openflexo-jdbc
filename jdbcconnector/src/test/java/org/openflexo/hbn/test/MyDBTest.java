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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
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
@Deprecated
public class MyDBTest extends OpenflexoTestCase {

	static FlexoEditor editor;

	static JDBCTechnologyAdapter jdbcTA;

	private static DirectoryResourceCenter resourceCenter;

	/**
	 * Test the VP creation
	 * 
	 * @throws ModelDefinitionException
	 * @throws SaveResourceException
	 * @throws IOException
	 * @throws SQLException
	 */
	// @Test
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

		JDBCConnection connection = jdbcResource.getLoadedResourceData();
		connection.setAddress("jdbc:hsqldb:hsql://localhost/");
		connection.setUser("sa");
		connection.setPassword("");

		if (connection.getConnection() == null) {
			fail(connection.getException().getMessage());
		}

		assertNotNull(connection.getConnection());

		/*DatabaseMetaData meta = connection.getConnection().getMetaData();
		
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
		}*/

		assertNotNull(connection.getSchema());

		JDBCSchema schema = connection.getSchema();

		System.out.println("schema=" + schema);

	}

}
