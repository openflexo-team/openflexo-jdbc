/*
 * (c) Copyright 2013- Openflexo
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

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract JDBCConnection implementation using Pamela.
 * 
 * @author charlie
 * 
 */
public abstract class JDBCConnectionImpl extends FlexoObject.FlexoObjectImpl implements JDBCConnection {

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
		// TODO reinit
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
				getPropertyChangeSupport().firePropertyChange(CONNECTION, null, connection);
			} catch (SQLException e) {
				throw new IllegalStateException("JDBC connection can't be initialized", e);
			}
		}
        return connection;
    }
}
