/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

import org.openflexo.components.wizard.Wizard;
import org.openflexo.components.wizard.WizardDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.technologyadapter.jdbc.library.JDBCIconLibrary;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJdbcModel;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

import javax.swing.*;
import java.util.EventObject;
import java.util.logging.Logger;

public class CreateJdbcModelInitializer extends ActionInitializer<CreateJdbcModel, RepositoryFolder, FlexoObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public CreateJdbcModelInitializer(ControllerActionInitializer actionInitializer) {
		super(CreateJdbcModel.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<CreateJdbcModel> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateJdbcModel>() {
			@Override
			public boolean run(EventObject e, CreateJdbcModel action) {
				Wizard wizard = new CreateJdbcModelWizard(action, getController());
				WizardDialog dialog = new WizardDialog(wizard, getController());
				dialog.showDialog();
				if (dialog.getStatus() != FIBController.Status.VALIDATED) {
					// Operation cancelled
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateJdbcModel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateJdbcModel>() {
			@Override
			public boolean run(EventObject e, CreateJdbcModel action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateJdbcModel> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateJdbcModel>() {
			@Override
			public boolean handleException(FlexoException exception, CreateJdbcModel action) {
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return JDBCIconLibrary.JDBC_FILE_ICON;
	}

}
