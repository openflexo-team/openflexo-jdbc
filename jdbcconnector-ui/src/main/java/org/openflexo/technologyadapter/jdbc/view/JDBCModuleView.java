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

package org.openflexo.technologyadapter.jdbc.view;

import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Module view is typed with generally the resource data, but can be done with any TechnologyObject.
 */
public class JDBCModuleView extends FIBModuleView<JDBCConnection> {

	public static Resource JDBC_MODULE_VIEW_FIB = ResourceLocator.locateResource("Fib/JdbcModuleView.fib");

	private final FlexoPerspective perspective;

	public JDBCModuleView(JDBCConnection connection, FlexoController controller, FlexoPerspective perspective) {
		super(connection, controller, JDBC_MODULE_VIEW_FIB, controller.getTechnologyAdapter(FMLRTTechnologyAdapter.class).getLocales());
		this.perspective = perspective;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return perspective;
	}
}
