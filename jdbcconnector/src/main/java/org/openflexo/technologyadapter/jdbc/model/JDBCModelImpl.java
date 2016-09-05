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

package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

/**
 * Abstract Simple implementation. Using Pamela.
 * 
 * @author SomeOne
 * 
 */
public abstract class JDBCModelImpl implements JDBCModel {

    public JDBCModelImpl() {
    }

    public JDBCTechnologyAdapter getTechnologyAdapter() {
        FlexoResource<JDBCModel> resource = getResource();
        if (resource != null && resource.getServiceManager() != null) {
            FlexoServiceManager serviceManager = resource.getServiceManager();
            return serviceManager.getService(TechnologyAdapterService.class).getTechnologyAdapter(JDBCTechnologyAdapter.class);
        }
        return null;
    }

}
