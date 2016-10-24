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
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCTableRole.JDBCTableRoleImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;

import java.lang.reflect.Type;

@ModelEntity
@ImplementationClass(value = JDBCTableRoleImpl.class)
@XMLElement
public interface JDBCLineRole extends FlexoRole<JDBCLine> {

    abstract class JDBCLineRoleImpl extends JDBCRole<JDBCLine> implements JDBCLineRole {

        @Override
        public Type getType() {
            return JDBCConnection.class;
        }

        @Override
        public Class<? extends JDBCActorReference<JDBCLine>> getActorReferenceClass() {
            return JDBCLineActorReference.class;
        }
    }
}
