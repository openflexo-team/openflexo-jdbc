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

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.FMLIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCConnection;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

import java.awt.*;
import java.util.logging.Logger;

public class CreateJdbcConnectionWizard extends FlexoWizard {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateJdbcConnectionWizard.class.getPackage().getName());

	private final CreateJDBCConnection action;

	private final ConfigureJdbcConnection configureJdbcConnection;

	public CreateJdbcConnectionWizard(CreateJDBCConnection action, FlexoController controller) {
		super(controller);
		this.action = action;
		addStep(configureJdbcConnection = new ConfigureJdbcConnection());
	}

	@Override
	public String getWizardTitle() {
		return action.getLocales().localizedForKey("create_jdbc_model");
	}

	@Override
	public Image getDefaultPageImage() {
		// TODO change icon
		return IconFactory.getImageIcon(FMLIconLibrary.FLEXO_CONCEPT_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public ConfigureJdbcConnection getConfigureJdbcConnection() {
		return configureJdbcConnection;
	}

	/**
	 * This step is used to set {@link VirtualModel} to be used, as well as name and title of the {@link VirtualModelInstance}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/ConfigureJdbcConnection.fib")
	public class ConfigureJdbcConnection extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateJDBCConnection getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_jdbc_connection");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getAddress())) {
				setIssueMessage(action.getLocales().localizedForKey("no_connection_address_defined"), IssueMessageType.ERROR);
				return false;
			}

			return true;

		}

		public String getResourceName() {
			return action.getResourceName();
		}

		public void setResourceName(String newResourceName) {
			if (!newResourceName.equals(getResourceName())) {
				String oldValue = getResourceName();
				action.setResourceName(newResourceName);
				getPropertyChangeSupport().firePropertyChange("resourceName", oldValue, newResourceName);
				checkValidity();
			}
		}

		public String getAddress() {
			return action.getAddress();
		}

		public void setAddress(String newAddress) {
			if (!newAddress.equals(getAddress())) {
				String oldValue = getAddress();
				action.setAddress(newAddress);
				getPropertyChangeSupport().firePropertyChange("address", oldValue, newAddress);
				checkValidity();
			}
		}

		public String getUser() {
			return action.getUser();
		}

		public void setUser(String newUser) {
			if (!newUser.equals(getUser())) {
				String oldValue = getUser();
				action.setUser(newUser);
				getPropertyChangeSupport().firePropertyChange("user", oldValue, newUser);
				checkValidity();
			}
		}

		public String getPassword() {
			return action.getPassword();
		}

		public void setPassword(String newPassword) {
			if (!newPassword.equals(getPassword())) {
				String oldValue = getPassword();
				action.setPassword(newPassword);
				getPropertyChangeSupport().firePropertyChange("password", oldValue, newPassword);
				checkValidity();
			}
		}
	}
}
