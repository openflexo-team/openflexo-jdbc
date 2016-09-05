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
import org.openflexo.technologyadapter.jdbc.fml.JDBCRole.JDBCRoleImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCModel;

import java.lang.reflect.Type;

@ModelEntity
@ImplementationClass(value = JDBCRoleImpl.class)
@XMLElement
public interface JDBCRole extends FlexoRole<JDBCModel> {

    public JDBCTechnologyAdapter getModelSlotTechnologyAdapter();

    public abstract static class JDBCRoleImpl extends FlexoRoleImpl<JDBCModel> implements JDBCRole {

        public JDBCRoleImpl() {
            super();
        }

        /* (non-Javadoc)
         * @see org.openflexo.foundation.viewpoint.FlexoRole.FlexoRoleImpl#getType()
         */
        @Override
        public Type getType() {
            return JDBCModel.class;
        }

        /* (non-Javadoc)
         * @see org.openflexo.foundation.viewpoint.FlexoRole.FlexoRoleImpl#getPreciseType()
         */
        /*
        @Override
        public String getPreciseType() {
            return JDBCModel.class.getSimpleName();
        }
        */

        /* (non-Javadoc)
         * @see org.openflexo.foundation.viewpoint.FlexoRole#defaultCloningStrategy()
         */
        @Override
        public RoleCloningStrategy defaultCloningStrategy() {
            return RoleCloningStrategy.Reference;
        }

        /* (non-Javadoc)
         * @see org.openflexo.foundation.viewpoint.FlexoRole.FlexoRoleImpl#defaultBehaviourIsToBeDeleted()
         */
        @Override
        public boolean defaultBehaviourIsToBeDeleted() {
            return false;
        }

        /* (non-Javadoc)
         * @see org.openflexo.foundation.viewpoint.FlexoRole.FlexoRoleImpl#makeActorReference(java.lang.Object, org.openflexo.foundation.view.FlexoConceptInstance)
         */
        @Override
        public ActorReference<JDBCModel> makeActorReference(final JDBCModel object, final FlexoConceptInstance epi) {
            final VirtualModelInstanceModelFactory factory = (VirtualModelInstanceModelFactory) epi.getFactory();
            final ModelObjectActorReference<JDBCModel> returned = factory.newInstance(ModelObjectActorReference.class);
            returned.setFlexoRole(this);
            returned.setFlexoConceptInstance(epi);
            returned.setModellingElement(object);
            return returned;
        }

        /**
         * 
         * @return JDBC technology adapter in service manager.
         */
        @Override
        public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
            return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
        }
    }
}
