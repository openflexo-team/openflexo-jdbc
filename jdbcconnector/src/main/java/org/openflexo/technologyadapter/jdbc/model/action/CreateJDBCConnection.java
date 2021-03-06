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

package org.openflexo.technologyadapter.jdbc.model.action;

import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceRepository;

public class CreateJDBCConnection extends FlexoAction<CreateJDBCConnection, RepositoryFolder<JDBCResource, ?>, FlexoObject> {

	private static final Logger logger = Logger.getLogger(CreateJDBCConnection.class.getPackage().getName());

	public static FlexoActionFactory<CreateJDBCConnection, RepositoryFolder<JDBCResource, ?>, FlexoObject> actionType = new FlexoActionFactory<CreateJDBCConnection, RepositoryFolder<JDBCResource, ?>, FlexoObject>(
			"create_jdbc_connection", FlexoActionFactory.newMenu, FlexoActionFactory.defaultGroup, FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJDBCConnection makeNewAction(RepositoryFolder<JDBCResource, ?> focusedObject, Vector<FlexoObject> globalSelection,
				FlexoEditor editor) {
			return new CreateJDBCConnection(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder<JDBCResource, ?> object, Vector<FlexoObject> globalSelection) {
			// TODO check what should be done
			return object != null && object.getResourceRepository() instanceof JDBCResourceRepository;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder<JDBCResource, ?> object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJDBCConnection.actionType, RepositoryFolder.class);
	}

	private String resourceName = "test.jdbc";
	private String address = "jdbc:hsqldb:hsql://localhost/";
	private String user = "SA";
	private String password = "";

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private CreateJDBCConnection(RepositoryFolder<JDBCResource, ?> focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			return getTechnologyAdapter().getLocales();
		}
		return super.getLocales();
	}

	private JDBCTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			JDBCTechnologyAdapter technologyAdapter = getTechnologyAdapter();
			JDBCResourceFactory resourceFactory = technologyAdapter.getResourceFactory(JDBCResourceFactory.class);
			JDBCResource resource = resourceFactory.makeJDBCResource(resourceName, getFocusedObject());
			JDBCConnection model = resource.getResourceData();
			model.setAddress(getAddress());
			model.setUser(getUser());
			model.setPassword(getPassword());

		} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
			throw new FlexoException(e);
		}

	}

	@Override
	public boolean isValid() {
		return true;
	}
}
