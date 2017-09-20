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

package org.openflexo.technologyadapter.jdbc.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

@ModelEntity
@ImplementationClass(value = JDBCConnection.JDBCConnectionImpl.class)
@XMLElement
@Imports({ @Import(JDBCSchema.class), @Import(JDBCTable.class), @Import(JDBCColumn.class), @Import(JDBCResultSet.class),
		@Import(JDBCLine.class), @Import(JDBCValue.class), @Import(JDBCResultSetDescription.class) })
public interface JDBCConnection extends TechnologyObject<JDBCTechnologyAdapter>, ResourceData<JDBCConnection> {

	@PropertyIdentifier(type = JDBCDbType.DbType.class)
	String DB_TYPE = "dbtype";
	String ADDRESS = "address";
	String USER = "user";
	String PASSWORD = "password";
	String SCHEMA = "schema";
	String CONNECTION = "connection";

	@Getter(DB_TYPE)
	@XMLAttribute
	JDBCDbType.DbType getDbType();

	@Setter(DB_TYPE)
	void setDbType(JDBCDbType.DbType aType);

	@Getter(ADDRESS)
	@XMLAttribute
	String getAddress();

	@Setter(ADDRESS)
	void setAddress(String address);

	@Getter(USER)
	@XMLAttribute
	String getUser();

	@Setter(USER)
	void setUser(String user);

	@Getter(PASSWORD)
	@XMLAttribute
	String getPassword();

	@Setter(PASSWORD)
	void setPassword(String pass);

	@Getter(SCHEMA)
	JDBCSchema getSchema();

	@Getter(value = CONNECTION, ignoreType = true)
	Connection getConnection();

	JDBCResultSet select(JDBCResultSetDescription description);

	public SQLException getException();

	/**
	 * Abstract JDBCConnection implementation using Pamela.
	 *
	 * @author charlie
	 *
	 */
	abstract class JDBCConnectionImpl extends FlexoObjectImpl implements JDBCConnection {

		private static final Logger LOGGER = Logger.getLogger(JDBCConnection.class.getPackage().getName());

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

		@Override
		public JDBCTechnologyAdapter getTechnologyAdapter() {
			FlexoResource<JDBCConnection> resource = getResource();
			if (resource != null && resource.getServiceManager() != null) {
				FlexoServiceManager serviceManager = resource.getServiceManager();
				return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return null;
		}

		@Override
		public JDBCSchema getSchema() {
			if (schema == null) {
				JDBCFactory factory = SQLHelper.getFactory(this);
				schema = factory.newInstance(JDBCSchema.class);
				schema.init(this);
				getPropertyChangeSupport().firePropertyChange(SCHEMA, null, schema);
			}
			return schema;
		}

		@Override
		public Connection getConnection() {
			if (connection == null && getAddress() != null) {
				try {
					System.out.println("Open connection " + getAddress());
					connection = DriverManager.getConnection(getAddress(), getUser(), getPassword());
					getPropertyChangeSupport().firePropertyChange(CONNECTION, null, connection);
				} catch (SQLException e) {
					LOGGER.warning(e.getMessage());
					exception = e;
				}
			}
			return connection;
		}

		private SQLException exception;

		@Override
		public SQLException getException() {
			return exception;
		}

		@Override
		public JDBCResultSet select(JDBCResultSetDescription description) {
			JDBCFactory factory = SQLHelper.getFactory(this);
			try {
				return SQLHelper.select(factory, this, description);
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't select from '" + description.getFrom() + "' on '" + this.getAddress() + "'", e);
				return factory.emptyResultSet(this);
			}
		}
	}
}
