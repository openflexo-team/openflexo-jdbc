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

package org.openflexo.technologyadapter.jdbc.fml.editionaction;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.dbtype.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;

/**
 * {@link EditionAction} used to create an empty JDBC resource
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(CreateJDBCConnection.CreateJDBCResourceImpl.class)
@XMLElement
@FML("CreateJDBCConnection")
public interface CreateJDBCConnection extends AbstractCreateResource<JDBCModelSlot, JDBCConnection, JDBCTechnologyAdapter> {

	@PropertyIdentifier(type = JDBCDbType.class)
	String DB_TYPE = "dbtype";
	String USER = "user";
	String PASSWORD = "password";
	String ADDRESS = "address";

	@Getter(DB_TYPE)
	@XMLAttribute
	JDBCDbType getDbType();

	@Setter(DB_TYPE)
	void setDbType(JDBCDbType aDBType);

	@Getter(USER)
	@XMLAttribute
	DataBinding<String> getUser();

	@Setter(USER)
	void setUser(DataBinding<String> user);

	@Getter(PASSWORD)
	@XMLAttribute
	DataBinding<String> getPassword();

	@Setter(PASSWORD)
	void setPassword(DataBinding<String> password);

	@Getter(ADDRESS)
	@XMLAttribute
	DataBinding<String> getAddress();

	@Setter(ADDRESS)
	void setAddress(DataBinding<String> address);

	abstract class CreateJDBCResourceImpl extends AbstractCreateResourceImpl<JDBCModelSlot, JDBCConnection, JDBCTechnologyAdapter>
			implements CreateJDBCConnection {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(CreateJDBCResourceImpl.class.getPackage().getName());

		private DataBinding<String> user;
		private DataBinding<String> password;
		private DataBinding<String> address;

		@Override
		public Type getAssignableType() {
			return JDBCConnection.class;
		}

		@Override
		public DataBinding<String> getUser() {
			if (user == null) {
				user = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				user.setBindingName(USER);
			}
			return user;
		}

		@Override
		public void setUser(DataBinding<String> user) {
			if (user != null) {
				user.setOwner(this);
				user.setDeclaredType(String.class);
				user.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				user.setBindingName(USER);
			}
			this.user = user;
		}

		@Override
		public DataBinding<String> getPassword() {
			if (password == null) {
				password = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				password.setBindingName(PASSWORD);
			}
			return password;
		}

		@Override
		public void setPassword(DataBinding<String> password) {
			if (password != null) {
				password.setOwner(this);
				password.setDeclaredType(String.class);
				password.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				password.setBindingName(PASSWORD);
			}
			this.password = password;
		}

		@Override
		public DataBinding<String> getAddress() {
			if (address == null) {
				address = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				address.setBindingName(ADDRESS);
			}
			return address;
		}

		@Override
		public void setAddress(DataBinding<String> address) {
			if (address != null) {
				address.setOwner(this);
				address.setDeclaredType(String.class);
				address.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				address.setBindingName(ADDRESS);
			}
			this.address = address;
		}

		@Override
		public JDBCConnection execute(RunTimeEvaluationContext evaluationContext) throws FlexoException {

			try {
				String resourceName = getResourceName(evaluationContext);
				String resourceURI = getResourceURI(evaluationContext);
				FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);
				JDBCTechnologyAdapter technologyAdapter = getServiceManager().getTechnologyAdapterService()
						.getTechnologyAdapter(JDBCTechnologyAdapter.class);

				JDBCResource newResource = createResource(technologyAdapter, JDBCResourceFactory.class, rc, resourceName, resourceURI,
						getRelativePath(), ".jdbc", true);
				JDBCConnection connection = newResource.getResourceData();

				connection.setAddress(evaluateDataBinding(getAddress(), evaluationContext));
				connection.setUser(evaluateDataBinding(getUser(), evaluationContext));
				connection.setPassword(evaluateDataBinding(getPassword(), evaluationContext));
				connection.setDbType(getDbType() != null ? getDbType() : JDBCDbType.GENERIC);

				return connection;
			} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
				throw new FlexoException(e);
			}

		}
	}
}
