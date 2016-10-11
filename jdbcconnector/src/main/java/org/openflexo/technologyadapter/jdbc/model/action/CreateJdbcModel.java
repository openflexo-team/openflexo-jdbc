/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.jdbc.model.action;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;

import java.util.Vector;
import java.util.logging.Logger;

public class CreateJdbcModel extends FlexoAction<CreateJdbcModel, RepositoryFolder, FlexoObject> {

	private static final Logger logger = Logger.getLogger(CreateJdbcModel.class.getPackage().getName());

	public static FlexoActionType<CreateJdbcModel, RepositoryFolder, FlexoObject> actionType = new FlexoActionType<CreateJdbcModel, RepositoryFolder, FlexoObject>(
			"create_jdbc_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJdbcModel makeNewAction(RepositoryFolder focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new CreateJdbcModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<FlexoObject> globalSelection) {
			//if (object != null && object.getResourceRepository() instanceof JDBCResourceRepository) {
				return true;
			//}
			//return false;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJdbcModel.actionType, RepositoryFolder.class);
	}

	private String resourceName = "test.jdbc";
	private String address = "jdbc:hsqldb:mem:testLoad1";
	private String user = "user";
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

	CreateJdbcModel(RepositoryFolder focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
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
			TechnologyContextManager<JDBCTechnologyAdapter> technologyContextManager = (TechnologyContextManager<JDBCTechnologyAdapter>) technologyAdapter.getTechnologyContextManager();
			JDBCResource resource = resourceFactory.makeJDBCResource(resourceName, getFocusedObject(), technologyContextManager);
			resource.getModel().init(address, user, password);

		} catch (ModelDefinitionException e) {
			throw new FlexoException(e);
		}

	}

	@Override
	public boolean isValid() {
		return true;
	}
}
