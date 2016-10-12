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

package org.openflexo.technologyadapter.jdbc.rm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyContextManager;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCModel;

@ModelEntity
@ImplementationClass(JDBCResourceImpl.class)
@XMLElement
public interface JDBCResource
extends
        PamelaResource<JDBCModel, JDBCFactory>,
        FlexoResource<JDBCModel>,
        TechnologyAdapterResource<JDBCModel, JDBCTechnologyAdapter>
{
    
    String TECHNOLOGY_CONTEXT_MANAGER = "technologyContextManager";

    @Getter(value=TECHNOLOGY_CONTEXT_MANAGER, ignoreType=true)
    JDBCTechnologyContextManager getTechnologyContextManager();

    @Setter(TECHNOLOGY_CONTEXT_MANAGER)
    void setTechnologyContextManager(JDBCTechnologyContextManager paramJDBCTechnologyContextManager);

    // TODO connect to model
    @Getter("model")
    JDBCModel getModel();
}
