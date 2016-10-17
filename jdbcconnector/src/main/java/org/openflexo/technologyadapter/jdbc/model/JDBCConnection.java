/*
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.jdbc.model;


import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@ModelEntity
@ImplementationClass(value = JDBCConnection.JDBCConnectionImpl.class)
@XMLElement
@Imports({ @Import(JDBCSchema.class), @Import(JDBCTable.class), @Import(JDBCColumn.class)})
public interface JDBCConnection extends TechnologyObject<JDBCTechnologyAdapter>, ResourceData<JDBCConnection> {

    String ADDRESS = "address";
    String USER = "user";
    String PASSWORD = "password";
    String SCHEMA = "schema";
    String CONNECTION = "connection";

    @Getter(ADDRESS) @XMLAttribute
    String getAddress();

    @Setter(ADDRESS)
    void setAddress(String address);

    @Getter(USER) @XMLAttribute
    String getUser();

    @Setter(USER)
    void setUser(String user);

    @Getter(PASSWORD) @XMLAttribute
    String getPassword();

    @Setter(PASSWORD)
    void setPassword(String pass);

    @Getter(SCHEMA)
    JDBCSchema getSchema();

    @Getter(value = CONNECTION, ignoreType = true)
    Connection getConnection();

    /**
	 * Abstract JDBCConnection implementation using Pamela.
	 *
	 * @author charlie
	 *
	 */
	abstract class JDBCConnectionImpl extends FlexoObjectImpl implements JDBCConnection {

		private JDBCSchema schema;

		private Connection connection;

		public JDBCConnectionImpl() {
		}

		@Override
		public void setAddress(String address) {
			performSuperSetter(ADDRESS, address);
			reinitConnection();
		}

		@Override
		public void setUser(String user) {
			performSuperSetter(USER, user);
			reinitConnection();
		}

		@Override
		public void setPassword(String password) {
			performSuperSetter(PASSWORD, password);
			reinitConnection();
		}

		private void reinitConnection() {
			Connection oldValue = connection;
			connection = null;
			getPropertyChangeSupport().firePropertyChange(CONNECTION, oldValue, null);
		}

		public JDBCTechnologyAdapter getTechnologyAdapter() {
			FlexoResource<JDBCConnection> resource = getResource();
			if (resource != null && resource.getServiceManager() != null) {
				FlexoServiceManager serviceManager = resource.getServiceManager();
				return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return null;
		}

		@Override
		public JDBCSchema getSchema()  {
			if (schema == null) {
				// Find the correct factory
				ModelFactory factory = null;
				try {
					factory = new ModelFactory(JDBCConnection.class);
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
				// JDBCFactory factory = ((JDBCResource) getResource()).getFactory();

				schema = factory.newInstance(JDBCSchema.class);
				schema.init(this);
				getPropertyChangeSupport().firePropertyChange(SCHEMA, null, schema);
			}
			return schema;
		}

		@Override
		public Connection getConnection() {
			if (connection == null) {
				try {
					connection = DriverManager.getConnection(getAddress(), getUser(), getPassword());
					createTableTest1(connection);
					createTableTest2(connection);

					getPropertyChangeSupport().firePropertyChange(CONNECTION, null, connection);
				} catch (SQLException e) {
					throw new IllegalStateException("JDBC connection can't be initialized", e);
				}
			}
			return connection;
		}

		/** Creates through SQL a table for one connection */
		private void createTable(Connection connection, String name, String[] ... attributes) throws SQLException {
			StringBuilder request = new StringBuilder("CREATE TABLE ");
			request.append(name);
			request.append(" (");
			int length = request.length();

			for (String[] attribute : attributes) {
				if (length < request.length()) request.append(", ");

				int localLength = request.length();
				for (String part : attribute) {
					if (localLength < request.length()) request.append(" ");
					request.append(part);
				}
			}
			request.append(")");

			connection.prepareCall(request.toString()).execute();
		}


		private void createTableTest1(Connection connection) throws SQLException {
			String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
			String[] name = {"name", "VARCHAR(100)"};
			createTable(connection, "test1", id, name);
		}

		private void createTableTest2(Connection connection) throws SQLException {
			String[] id = {"id", "INT", "PRIMARY KEY", "NOT NULL"};
			String[] name = {"name", "VARCHAR(100)"};
			String[] lastName = {"lastname", "VARCHAR(100)"};
			String[] description = {"description", "VARCHAR(500)"};
			String[] portrait = {"portrait", "VARCHAR(200)"};
			createTable(connection, "test2", id, name, lastName, description, portrait);
		}

	}
}
