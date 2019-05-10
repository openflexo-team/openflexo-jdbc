/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Freemodellingeditor, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.jdbc.controller.action;

import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoActionWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.jdbc.library.JDBCIconLibrary;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCConnection;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateJDBCConnectionWizard extends FlexoActionWizard<CreateJDBCConnection> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateJDBCConnectionWizard.class.getPackage().getName());

	private final ConfigureJDBCConnection configureJdbcConnection;

	public CreateJDBCConnectionWizard(CreateJDBCConnection action, FlexoController controller) {
		super(action, controller);
		addStep(configureJdbcConnection = new ConfigureJDBCConnection());
	}

	@Override
	public String getWizardTitle() {
		return getAction().getLocales().localizedForKey("create_jdbc_model");
	}

	@Override
	public Image getDefaultPageImage() {
		// TODO change icon
		return IconFactory.getImageIcon(JDBCIconLibrary.JDBC_TECHNOLOGY_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public ConfigureJDBCConnection getConfigureJdbcConnection() {
		return configureJdbcConnection;
	}

	@FIBPanel("Fib/Wizard/ConfigureJDBCConnection.fib")
	public class ConfigureJDBCConnection extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateJDBCConnection getAction() {
			return CreateJDBCConnectionWizard.this.getAction();
		}

		@Override
		public String getTitle() {
			return getAction().getLocales().localizedForKey("configure_jdbc_connection");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getAddress())) {
				setIssueMessage(getAction().getLocales().localizedForKey("no_connection_address_defined"), IssueMessageType.ERROR);
				return false;
			}

			return true;

		}

		public String getResourceName() {
			return getAction().getResourceName();
		}

		public void setResourceName(String newResourceName) {
			if (!newResourceName.equals(getResourceName())) {
				String oldValue = getResourceName();
				getAction().setResourceName(newResourceName);
				getPropertyChangeSupport().firePropertyChange("resourceName", oldValue, newResourceName);
				checkValidity();
			}
		}

		public String getAddress() {
			return getAction().getAddress();
		}

		public void setAddress(String newAddress) {
			if (!newAddress.equals(getAddress())) {
				String oldValue = getAddress();
				getAction().setAddress(newAddress);
				getPropertyChangeSupport().firePropertyChange("address", oldValue, newAddress);
				checkValidity();
			}
		}

		public String getUser() {
			return getAction().getUser();
		}

		public void setUser(String newUser) {
			if (!newUser.equals(getUser())) {
				String oldValue = getUser();
				getAction().setUser(newUser);
				getPropertyChangeSupport().firePropertyChange("user", oldValue, newUser);
				checkValidity();
			}
		}

		public String getPassword() {
			return getAction().getPassword();
		}

		public void setPassword(String newPassword) {
			if (!newPassword.equals(getPassword())) {
				String oldValue = getPassword();
				getAction().setPassword(newPassword);
				getPropertyChangeSupport().firePropertyChange("password", oldValue, newPassword);
				checkValidity();
			}
		}
	}

}
