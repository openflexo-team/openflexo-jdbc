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

package org.openflexo.technologyadapter.jdbc;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.FMLRTModelSlot;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.InferedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Implementation of the ModelSlot class for the JDBC technology adapter<br>
 * 
 * Managed modelling elements are {@link JDBCTable}, {@link JDBCColumn} and {@link JDBCLine}
 * 
 * @author charlie
 * 
 */
@ModelEntity
@XMLElement
@ImplementationClass(HbnModelSlot.HbnModelSlotImpl.class)
/*@DeclareActorReferences({ JDBCTableActorReference.class, JDBCColumnActorReference.class, JDBCLineActorReference.class })
@DeclareFlexoRoles({ JDBCTableRole.class, JDBCColumnRole.class, JDBCLineRole.class })
@DeclareEditionActions({ CreateJDBCResource.class, AddJDBCTable.class })
@DeclareFetchRequests({ SelectJDBCTable.class, SelectJDBCColumn.class, SelectJDBCLine.class })*/
public interface HbnModelSlot extends InferedFMLRTModelSlot<HbnVirtualModelInstance, JDBCTechnologyAdapter> {

	abstract class HbnModelSlotImpl extends InferedFMLRTModelSlotImpl<HbnVirtualModelInstance, JDBCTechnologyAdapter>
			implements HbnModelSlot {

		@Override
		public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return super.defaultFlexoRoleName(flexoRoleClass);
		}

		@Override
		public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (JDBCTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		/*@Override
		public ModelSlotInstanceConfiguration<? extends FreeModelSlot<JDBCConnection>, JDBCConnection> createConfiguration(
				FlexoConceptInstance fci, FlexoResourceCenter<?> rc) {
			return new JDBCModelSlotInstanceConfiguration(this, fci, rc);
		}
		
		@Override
		public TechnologyAdapterResource<JDBCConnection, ?> createProjectSpecificEmptyResource(VirtualModelInstance<?, ?> view,
				String filename, String modelUri) {
			// TODO create empty resource
			return null;
		}
		
		@Override
		public TechnologyAdapterResource<JDBCConnection, ?> createSharedEmptyResource(FlexoResourceCenter<?> resourceCenter,
				String relativePath, String filename, String modelUri) {
			// TODO create empty resource
			return null;
		}*/

		@Override
		public ModelSlotInstanceConfiguration<? extends FMLRTModelSlot<HbnVirtualModelInstance, JDBCTechnologyAdapter>, HbnVirtualModelInstance> createConfiguration(
				FlexoConceptInstance fci, FlexoResourceCenter<?> rc) {
			// TODO Auto-generated method stub
			return super.createConfiguration(fci, rc);
		}

	}
}
