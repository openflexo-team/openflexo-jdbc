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

package org.openflexo.technologyadapter.jdbc.controller;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.controller.action.CreateJdbcConnectionInitializer;
import org.openflexo.technologyadapter.jdbc.library.JDBCIconLibrary;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.view.JDBCModuleView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

import javax.swing.*;
import java.util.logging.Logger;

public class JDBCAdapterController extends TechnologyAdapterController<JDBCTechnologyAdapter> {
    
	static final Logger LOGGER = Logger.getLogger(JDBCAdapterController.class.getPackage().getName());

	private InspectorGroup jdbcInspectorGroup;


	@Override
	public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
		return JDBCTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		new CreateJdbcConnectionInitializer(actionInitializer);
	}

    @Override
    protected void initializeInspectors(FlexoController controller) {
		//jdbcInspectorGroup = controller.loadInspectorGroup("JDBC", getTechnologyAdapter().getLocales(), getFMLTechnologyAdapterInspectorGroup());

		jdbcInspectorGroup = controller.loadInspectorGroup("JDBC", getTechnologyAdapter().getLocales(), getFMLTechnologyAdapterInspectorGroup());

	}

    @Override
    public InspectorGroup getTechnologyAdapterInspectorGroup() {
        return jdbcInspectorGroup;
    }

    @Override
	public ImageIcon getTechnologyBigIcon() {
		return JDBCIconLibrary.JDBC_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return JDBCIconLibrary.JDBC_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		return JDBCIconLibrary.JDBC_FILE_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return JDBCIconLibrary.JDBC_FILE_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(final Class<? extends TechnologyObject<?>> objectClass) {
		return JDBCIconLibrary.JDBC_TECHNOLOGY_ICON;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(final TechnologyObject<JDBCTechnologyAdapter> object, final FlexoController controller, final FlexoPerspective perspective) {
		// TODO Auto-generated method stub : update your moduleView code to have somethig represented
		if (object instanceof JDBCConnection){
			return new JDBCModuleView((JDBCConnection) object, controller, perspective);
		}
		return new EmptyPanel<>(controller, perspective, object);
	}

    @Override
    public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> flexoRoleClass) {
		return JDBCIconLibrary.JDBC_TECHNOLOGY_ICON;
    }

	@Override
	public String getWindowTitleforObject(TechnologyObject<JDBCTechnologyAdapter> obj, FlexoController controller) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean hasModuleViewForObject(TechnologyObject<JDBCTechnologyAdapter> obj, FlexoController controller) {
		// TODO Auto-generated method stub
		return obj instanceof JDBCConnection;
	}
}

