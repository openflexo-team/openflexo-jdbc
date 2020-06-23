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

import javax.swing.ImageIcon;

import org.openflexo.fml.rt.controller.view.VirtualModelInstanceView;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.icon.FMLRTIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.controller.action.CreateJDBCConnectionInitializer;
import org.openflexo.technologyadapter.jdbc.controller.action.CreateJDBCMappingVirtualModelInitializer;
import org.openflexo.technologyadapter.jdbc.controller.action.CreateJDBCVirtualModelInitializer;
import org.openflexo.technologyadapter.jdbc.fml.JDBCColumnRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableRole;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCConnection;
import org.openflexo.technologyadapter.jdbc.hbn.fml.AbstractPerformSQLQuery;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CommitTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CreateHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CreateHbnResource;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnColumnRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnInitializer;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnOneToManyReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.OpenTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.RefreshHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.fml.RollbackTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.SaveHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.library.JDBCIconLibrary;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.view.JDBCModuleView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class JDBCAdapterController extends TechnologyAdapterController<JDBCTechnologyAdapter> {

	private InspectorGroup jdbcInspectorGroup;

	@Override
	public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
		return JDBCTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		new CreateJDBCConnectionInitializer(actionInitializer);
		new CreateJDBCVirtualModelInitializer(actionInitializer);
		new CreateJDBCMappingVirtualModelInitializer(actionInitializer);
	}

	@Override
	protected void initializeInspectors(FlexoController controller) {
		// jdbcInspectorGroup = controller.loadInspectorGroup("JDBC", getTechnologyAdapter().getLocales(),
		// getFMLTechnologyAdapterInspectorGroup());

		jdbcInspectorGroup = controller.loadInspectorGroup("JDBC", getTechnologyAdapter().getLocales(),
				getFMLTechnologyAdapterInspectorGroup());

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
		if (HbnVirtualModelInstance.class.isAssignableFrom(objectClass)) {
			return IconFactory.getImageIcon(JDBCIconLibrary.JDBC_TECHNOLOGY_ICON, FMLRTIconLibrary.VIRTUAL_MODEL_INSTANCE_MARKER);
		}
		return JDBCIconLibrary.JDBC_TECHNOLOGY_ICON;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(final TechnologyObject<JDBCTechnologyAdapter> object, final FlexoController controller,
			final FlexoPerspective perspective) {
		if (object instanceof JDBCConnection) {
			return new JDBCModuleView((JDBCConnection) object, controller, perspective);
		}
		if (object instanceof HbnVirtualModelInstance) {
			return new VirtualModelInstanceView((HbnVirtualModelInstance) object, controller, perspective);
		}
		return new EmptyPanel<>(controller, perspective, object);
	}

	@Override
	public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> flexoRoleClass) {
		if (HbnColumnRole.class.isAssignableFrom(flexoRoleClass)) {
			return JDBCIconLibrary.JDBC_COLUMN_ICON;
		}
		if (HbnToOneReferenceRole.class.isAssignableFrom(flexoRoleClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.SYNC);
		}
		if (HbnOneToManyReferenceRole.class.isAssignableFrom(flexoRoleClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.SYNC);
		}
		if (JDBCTableRole.class.isAssignableFrom(flexoRoleClass)) {
			return JDBCIconLibrary.JDBC_TABLE_ICON;
		}
		if (JDBCColumnRole.class.isAssignableFrom(flexoRoleClass)) {
			return JDBCIconLibrary.JDBC_COLUMN_ICON;
		}
		if (JDBCLineRole.class.isAssignableFrom(flexoRoleClass)) {
			return JDBCIconLibrary.JDBC_ROW_ICON;
		}
		return JDBCIconLibrary.JDBC_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getIconForFlexoBehaviour(Class<? extends FlexoBehaviour> flexoBehaviourClass) {
		if (HbnInitializer.class.isAssignableFrom(flexoBehaviourClass)) {
			return IconFactory.getImageIcon(JDBCIconLibrary.JDBC_TECHNOLOGY_ICON, IconLibrary.SYNC);
		}
		return super.getIconForFlexoBehaviour(flexoBehaviourClass);
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject<JDBCTechnologyAdapter> obj, FlexoController controller) {
		if (obj instanceof JDBCConnection) {
			return "Connection to " + ((JDBCConnection) obj).getAddress();
		}
		return "Connection";
	}

	@Override
	public boolean hasModuleViewForObject(TechnologyObject<JDBCTechnologyAdapter> obj, FlexoController controller) {
		if (obj instanceof HbnVirtualModelInstance) {
			return true;
		}
		if (obj instanceof JDBCConnection) {
			return true;
		}
		return false;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (CreateJDBCConnection.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(JDBCIconLibrary.JDBC_TECHNOLOGY_ICON, IconLibrary.NEW_MARKER);
		}
		if (CreateHbnResource.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(JDBCIconLibrary.JDBC_TECHNOLOGY_ICON, IconLibrary.NEW_MARKER);
		}
		if (AbstractPerformSQLQuery.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.PERFORM_SQL_QUERY_ICON;
		}
		if (OpenTransaction.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.OPEN_TRANSACTION_ICON;
		}
		if (CommitTransaction.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.COMMIT_TRANSACTION_ICON;
		}
		if (RollbackTransaction.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.ROLLBACK_TRANSACTION_ICON;
		}
		if (CreateHbnObject.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.DUPLICATE);
		}
		if (SaveHbnObject.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.SAVE_ICON;
		}
		if (RefreshHbnObject.class.isAssignableFrom(editionActionClass)) {
			return JDBCIconLibrary.REFRESH_ICON;
		}
		return super.getIconForEditionAction(editionActionClass);
	}

}
