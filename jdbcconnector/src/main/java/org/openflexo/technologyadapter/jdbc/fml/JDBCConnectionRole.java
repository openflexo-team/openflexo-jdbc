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

package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ModelObjectActorReference;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceModelFactory;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.fml.JDBCConnectionRole.JDBCConnectionRoleImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;

import java.lang.reflect.Type;

@ModelEntity
@ImplementationClass(value = JDBCConnectionRoleImpl.class)
@XMLElement
public interface JDBCConnectionRole extends FlexoRole<JDBCConnection> {

    JDBCTechnologyAdapter getModelSlotTechnologyAdapter();

    abstract static class JDBCConnectionRoleImpl extends FlexoRoleImpl<JDBCConnection> implements JDBCConnectionRole {

        public JDBCConnectionRoleImpl() {
            super();
        }

        @Override
        public Type getType() {
            return JDBCConnection.class;
        }

        @Override
        public RoleCloningStrategy defaultCloningStrategy() {
            return RoleCloningStrategy.Reference;
        }

        @Override
        public boolean defaultBehaviourIsToBeDeleted() {
            return false;
        }

        @Override
        public ActorReference<JDBCConnection> makeActorReference(final JDBCConnection object, final FlexoConceptInstance epi) {
            final VirtualModelInstanceModelFactory factory = (VirtualModelInstanceModelFactory) epi.getFactory();
            final ModelObjectActorReference<JDBCConnection> returned = factory.newInstance(ModelObjectActorReference.class);
            returned.setFlexoRole(this);
            returned.setFlexoConceptInstance(epi);
            returned.setModellingElement(object);
            return returned;
        }

        @Override
        public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
            return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
        }
    }
}
