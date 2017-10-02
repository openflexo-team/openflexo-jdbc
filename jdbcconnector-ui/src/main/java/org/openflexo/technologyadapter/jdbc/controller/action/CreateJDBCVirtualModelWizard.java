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

package org.openflexo.technologyadapter.jdbc.controller.action;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.FMLIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.jdbc.library.JDBCIconLibrary;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModel;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModel.TableMapping;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateJDBCVirtualModelWizard extends FlexoWizard {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateJDBCVirtualModelWizard.class.getPackage().getName());

	private final CreateJDBCVirtualModel action;

	private final ConfigureJDBCVirtualModel configureJdbcConnection;
	private ChooseEntitiesToBeReflected chooseEntitiesToBeReflected;

	private static final Dimension DIMENSIONS = new Dimension(600, 500);

	public CreateJDBCVirtualModelWizard(CreateJDBCVirtualModel action, FlexoController controller) {
		super(controller);
		this.action = action;
		addStep(configureJdbcConnection = new ConfigureJDBCVirtualModel());
	}

	@Override
	public String getWizardTitle() {
		return action.getLocales().localizedForKey("create_virtual_model_reflecting_a_jdbc_schema");
	}

	@Override
	public Image getDefaultPageImage() {
		// TODO change icon
		return IconFactory.getImageIcon(FMLIconLibrary.VIRTUAL_MODEL_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public ConfigureJDBCVirtualModel getConfigureJdbcConnection() {
		return configureJdbcConnection;
	}

	@Override
	public Dimension getPreferredSize() {
		return DIMENSIONS;
	}

	/**
	 * This step is used to set {@link VirtualModel} to be used, as well as name and title of the {@link FMLRTVirtualModelInstance}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/ConfigureJDBCVirtualModel.fib")
	public class ConfigureJDBCVirtualModel extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateJDBCVirtualModel getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_jdbc_connection");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getNewVirtualModelName())) {
				setIssueMessage(action.getLocales().localizedForKey("please_enter_name_for_new_virtual_model"), IssueMessageType.ERROR);
				return false;
			}

			if (StringUtils.isEmpty(getAddress())) {
				setIssueMessage(action.getLocales().localizedForKey("no_connection_address_defined"), IssueMessageType.ERROR);
				return false;
			}

			return true;

		}

		public String getNewVirtualModelName() {
			return action.getNewVirtualModelName();
		}

		public void setNewVirtualModelName(String newVirtualModelName) {
			if (!newVirtualModelName.equals(getNewVirtualModelName())) {
				String oldValue = getNewVirtualModelName();
				action.setNewVirtualModelName(newVirtualModelName);
				getPropertyChangeSupport().firePropertyChange("newVirtualModelName", oldValue, newVirtualModelName);
				checkValidity();
			}
		}

		public JDBCDbType getDbType() {
			return action.getDbType();
		}

		public void setDbType(JDBCDbType aDbType) {
			if (!aDbType.equals(getDbType())) {
				JDBCDbType oldValue = getDbType();
				action.setDbType(aDbType);
				getPropertyChangeSupport().firePropertyChange("dbType", oldValue, aDbType);
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

		@Override
		public boolean isTransitionalStep() {
			return true;
		}

		@Override
		public void performTransition() {

			if (getAction().getJDBCConnection() != null) {
				addStep(chooseEntitiesToBeReflected = new ChooseEntitiesToBeReflected());
			}
		}

		@Override
		public void discardTransition() {
			if (chooseEntitiesToBeReflected != null) {
				removeStep(chooseEntitiesToBeReflected);
				chooseEntitiesToBeReflected = null;
			}
		}

	}

	@FIBPanel("Fib/Wizard/ChooseEntitiesToBeReflected.fib")
	public class ChooseEntitiesToBeReflected extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateJDBCVirtualModel getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("choose_entities_to_be_reflected");
		}

		@Override
		public boolean isValid() {

			if (getAction().getJDBCConnection().getConnection() == null) {
				setIssueMessage(getAction().getJDBCConnection().getException().getMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (getAction().getTablesToBeReflected() == null || getAction().getTablesToBeReflected().size() == 0) {
				setIssueMessage(action.getLocales().localizedForKey("please_choose_some_entities"), IssueMessageType.ERROR);
				return false;
			}

			return true;
		}

		public void changedSelection() {
			getPropertyChangeSupport().firePropertyChange("tablesToBeReflected", null, getAction().getTablesToBeReflected());
			checkValidity();
		}

		public ImageIcon getTableIcon() {
			return JDBCIconLibrary.JDBC_TABLE_ICON;
		}

		@Override
		public boolean isTransitionalStep() {
			return true;
		}

		private List<ConfigureTableMapping> configureTableMappings;

		@Override
		public void performTransition() {
			configureTableMappings = new ArrayList<>();
			for (TableMapping tm : getAction().getTableMappings()) {
				ConfigureTableMapping newCTM = new ConfigureTableMapping(tm);
				configureTableMappings.add(newCTM);
				addStep(newCTM);
			}
		}

		@Override
		public void discardTransition() {
			for (ConfigureTableMapping ctm : configureTableMappings) {
				removeStep(ctm);
			}
			configureTableMappings.clear();
			getAction().clearTableMappings();
		}

	}

	@FIBPanel("Fib/Wizard/ConfigureTableMapping.fib")
	public class ConfigureTableMapping extends WizardStep {

		private TableMapping tableMapping;

		public ConfigureTableMapping(TableMapping tableMapping) {
			this.tableMapping = tableMapping;
		}

		public TableMapping getTableMapping() {
			return tableMapping;
		}

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateJDBCVirtualModel getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_table_mapping_for") + " " + tableMapping.getTable().getName();
		}

		@Override
		public boolean isValid() {

			if (getAction().getJDBCConnection().getConnection() == null) {
				setIssueMessage(getAction().getJDBCConnection().getException().getMessage(), IssueMessageType.ERROR);
				return false;
			}

			return true;
		}

		public ImageIcon getColumnIcon(JDBCColumn column) {
			if (column.isPrimaryKey()) {
				return JDBCIconLibrary.JDBC_KEY_ICON;
			}
			return JDBCIconLibrary.JDBC_COLUMN_ICON;
		}
	}

}
