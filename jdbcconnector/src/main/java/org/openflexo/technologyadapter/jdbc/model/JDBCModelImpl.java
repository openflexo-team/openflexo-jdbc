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
 * Abstract JDBCModel implementation using Pamela.
 * 
 * @author charlie
 * 
 */
public abstract class JDBCModelImpl extends FlexoObject.FlexoObjectImpl implements JDBCModel {

    private Connection connection = null;

	private JDBCSchema schema;

    public JDBCModelImpl() {
    }

	@Override
	public void setAddress(String address) {
		performSuperSetter(ADDRESS, address);
		close();
	}

	@Override
	public void setUser(String user) {
		performSuperSetter(USER, user);
		close();
	}

	@Override
	public void setPassword(String password) {
		performSuperSetter(PASSWORD, password);
		close();
	}

	public void close() {
		connection = null;
	}

	@Override
	public void connect() throws SQLException {
		if (connection != null) {
			connection = DriverManager.getConnection(getAddress(), getUser(), getPassword());
		}
	}

	public JDBCTechnologyAdapter getTechnologyAdapter() {
        FlexoResource<JDBCModel> resource = getResource();
        if (resource != null && resource.getServiceManager() != null) {
            FlexoServiceManager serviceManager = resource.getServiceManager();
            return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(JDBCTechnologyAdapter.class);
        }
        return null;
    }

	@Override
	public JDBCSchema getSchema() throws SQLException {
		if (schema == null) {
			// Find the correct factory
			ModelFactory factory = null;
			try {
				factory = new ModelFactory(JDBCModel.class);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
			// JDBCFactory factory = ((JDBCResource) getResource()).getFactory();

			schema = factory.newInstance(JDBCSchema.class);
			schema.init(this);
		}
		return schema;
	}

	@Override
    public Connection getConnection() {
		if (connection == null) throw new IllegalStateException("JDBC connection not initialized");
        return connection;
    }
}
