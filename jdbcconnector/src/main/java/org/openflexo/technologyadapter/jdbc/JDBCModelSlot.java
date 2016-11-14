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
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstance;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCColumnActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCColumnRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineRole;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableActorReference;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableRole;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.AddJDBCTable;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCResource;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCColumn;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCLine;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;

import java.lang.reflect.Type;

/**
 * Implementation of the ModelSlot class for the JDBC technology adapter<br>
 * We expect here to connect an JDBC model conform to an JDBCMetaModel
 * 
 * @author SomeOne
 * 
 */
@DeclareActorReferences({JDBCTableActorReference.class, JDBCColumnActorReference.class, JDBCLineActorReference.class})
@DeclareFlexoRoles({JDBCTableRole.class, JDBCColumnRole.class, JDBCLineRole.class})
@DeclareEditionActions({ CreateJDBCResource.class, AddJDBCTable.class})
@DeclareFetchRequests({ SelectJDBCTable.class, SelectJDBCColumn.class, SelectJDBCLine.class })
@ModelEntity
@ImplementationClass(JDBCModelSlot.JDBCModelSlotImpl.class)
@XMLElement
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

        @Override
        public ModelSlotInstanceConfiguration<? extends FreeModelSlot<JDBCConnection>, JDBCConnection> createConfiguration(AbstractVirtualModelInstance<?, ?> virtualModelInstance, FlexoResourceCenter<?> rc) {
            return new JDBCModelSlotInstanceConfiguration(this, virtualModelInstance, rc);
        }

        @Override
        public TechnologyAdapterResource<JDBCConnection, ?> createProjectSpecificEmptyResource(View view, String filename, String modelUri) {
            // TODO create empty resource
            return null;
        }
    }
}
