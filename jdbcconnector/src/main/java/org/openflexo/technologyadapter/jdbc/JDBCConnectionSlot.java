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
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCConnectionRole;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;

import java.lang.reflect.Type;

/**
 * Implementation of the ModelSlot class for the JDBC technology adapter<br>
 * We expect here to connect an JDBC model conform to an JDBCMetaModel
 * 
 * @author SomeOne
 * 
 */
@DeclareFlexoRoles({JDBCConnectionRole.class})
@DeclareEditionActions({})
@DeclareFetchRequests({})
@ModelEntity
@ImplementationClass(JDBCConnectionSlot.JDBCConnectionSlotImpl.class)
@XMLElement
public interface JDBCConnectionSlot extends FreeModelSlot<JDBCConnection> {

    @Override
    JDBCTechnologyAdapter getModelSlotTechnologyAdapter();

    abstract class JDBCConnectionSlotImpl extends FreeModelSlotImpl<JDBCConnection> implements JDBCConnectionSlot {

        @Override
        public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
            return JDBCTechnologyAdapter.class;
        }

        @Override
        public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> patternRoleClass) {
            if (JDBCConnectionRole.class.isAssignableFrom(patternRoleClass)) {
                return "Object";
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
