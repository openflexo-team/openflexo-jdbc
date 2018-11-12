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

import java.lang.reflect.Type;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCColumnActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCColumnRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableRole;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.AddJDBCTable;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCConnection;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCColumn;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCLine;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * An implementation of a {@link ModelSlot} providing basic access to JDBC modelling element<br>
 * 
 * This is really low-level, as managed modelling elements are {@link JDBCTable}, {@link JDBCColumn} and {@link JDBCLine}
 * 
 * In production context, you should use {@link HbnModelSlot}
 * 
 * @see HbnModelSlot
 * @author charlie
 * 
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCModelSlot.JDBCModelSlotImpl.class)
@DeclareActorReferences({ JDBCTableActorReference.class, JDBCColumnActorReference.class, JDBCLineActorReference.class })
@DeclareFlexoRoles({ JDBCTableRole.class, JDBCColumnRole.class, JDBCLineRole.class })
@DeclareEditionActions({ CreateJDBCConnection.class, AddJDBCTable.class })
@DeclareFetchRequests({ SelectJDBCTable.class, SelectJDBCColumn.class, SelectJDBCLine.class })
public interface JDBCModelSlot extends FreeModelSlot<JDBCConnection> {

	@Override
	JDBCTechnologyAdapter getModelSlotTechnologyAdapter();

	abstract class JDBCModelSlotImpl extends FreeModelSlotImpl<JDBCConnection> implements JDBCModelSlot {

		@Override
		public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> patternRoleClass) {
			if (JDBCTableRole.class.isAssignableFrom(patternRoleClass)) {
				return "table";
			}
			if (JDBCColumnRole.class.isAssignableFrom(patternRoleClass)) {
				return "column";
			}
			if (JDBCLineRole.class.isAssignableFrom(patternRoleClass)) {
				return "line";
			}
			return "";
		}

		@Override
		public Type getType() {
			return JDBCConnection.class;
		}

		@Override
		public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (JDBCTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

	}
}
