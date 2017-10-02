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

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
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
import org.openflexo.technologyadapter.jdbc.util.DriverWrapper;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

@ModelEntity
@ImplementationClass(value = JDBCConnection.JDBCConnectionImpl.class)
@XMLElement
@Imports({ @Import(JDBCSchema.class), @Import(JDBCTable.class), @Import(JDBCColumn.class), @Import(JDBCResultSet.class),
		@Import(JDBCLine.class), @Import(JDBCValue.class), @Import(JDBCResultSetDescription.class) })
public interface JDBCConnection extends TechnologyObject<JDBCTechnologyAdapter>, ResourceData<JDBCConnection> {

	@PropertyIdentifier(type = JDBCDbType.class)
	String DB_TYPE = "dbtype";
	String DRIVER_JAR_NAME = "driver_jar";
	String DRIVER_CLASS_NAME = "driver_class_name";
	String ADDRESS = "address";
	String USER = "user";
	String PASSWORD = "password";
	String SCHEMA = "schema";
	String CONNECTION = "connection";

	@Getter(DRIVER_CLASS_NAME)
	@XMLAttribute
	String getDriverClassName();

	@Setter(DRIVER_CLASS_NAME)
	void setDriverClassName(String className);

	@Getter(DRIVER_JAR_NAME)
	@XMLAttribute
	String getDriverJarName();

	@Setter(DRIVER_JAR_NAME)
	void setDriverJarName(String aType);

	@Getter(DB_TYPE)
	@XMLAttribute
	JDBCDbType getDbType();

	@Setter(DB_TYPE)
	void setDbType(JDBCDbType aType);

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

	/**
	 * returns a new SQLConnection, after loading driver if necesary
	 * 
	 * @return
	 */
	@Getter(value = CONNECTION, ignoreType = true)
	Connection getConnection();

	JDBCResultSet select(JDBCResultSetDescription description);

	public Exception getException();

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

		private Exception exception;

		@Override
		public Exception getException() {
			return exception;
		}

		public JDBCConnectionImpl() {
		}

		@Override
		public void setDriverJarName(String aName) {
			performSuperSetter(DRIVER_JAR_NAME, aName);
			reinitConnection();
		}

		@Override
		public void setDriverClassName(String aName) {
			performSuperSetter(DRIVER_CLASS_NAME, aName);
			reinitConnection();
		}

		@Override
		public void setDbType(JDBCDbType aType) {
			performSuperSetter(DB_TYPE, aType);
			reinitConnection();
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
			if (connection != null) {
				Connection oldValue = connection;
				connection = null;
				getPropertyChangeSupport().firePropertyChange(CONNECTION, oldValue, null);
			}
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

					// try DriverClass and DriverJAr info to connect
					String classname = getDriverClassName();
					if (classname != null) {

						Class<?> cl = null;
						try {
							cl = Class.forName(classname);
						} catch (ClassNotFoundException e) {
							LOGGER.warning("Cannot load JDBC Driver: " + e.getMessage());
						}

						try {
							if (cl == null & getDriverJarName() != null) {
								URL u;
								u = new URL("file:" + getDriverJarName());

								URLClassLoader ucl = new URLClassLoader(new URL[] { u });

								Driver d = (Driver) Class.forName(classname, true, ucl).newInstance();
								DriverManager.registerDriver(new DriverWrapper(d));
							}
						} catch (Exception e) {
							LOGGER.warning("Cannot load JDBC Driver: " + e.getMessage());
							exception = e;
							return null;
						}

						// Use DbType if set
						if (cl == null && getDbType() != null) {

							try {
								cl = Class.forName(getDbType().getDriverClassName());
							} catch (ClassNotFoundException e) {
								LOGGER.warning(e.getMessage());
								return null;
							}
						}
						if (cl == null) {
							LOGGER.warning("Cannot find any driver to connect to Database");
							return null;
						}
					}
					connection = DriverManager.getConnection(getAddress(), getUser(), getPassword());
					getPropertyChangeSupport().firePropertyChange(CONNECTION, null, connection);
				} catch (SQLException e) {
					LOGGER.warning(e.getMessage());
					exception = e;
				}
			}
			return connection;
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
