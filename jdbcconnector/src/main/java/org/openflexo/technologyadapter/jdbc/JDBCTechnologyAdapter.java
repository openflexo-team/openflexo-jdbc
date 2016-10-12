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

import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceTypes;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;

import java.util.logging.Logger;


/**
 * This class defines and implements the JDBC technology adapter
 * 
 * @author SomeOne
 * 
 */
@DeclareModelSlots({JDBCConnectionSlot.class})
@DeclareResourceTypes({JDBCResourceFactory.class})
public class JDBCTechnologyAdapter extends TechnologyAdapter {


    private static final Logger LOGGER = Logger.getLogger(JDBCTechnologyAdapter.class.getPackage().getName());

    public JDBCTechnologyAdapter() throws TechnologyAdapterInitializationException {
    }

    @Override
    public String getIdentifier() {
        return "JDBC";
    }

    @Override
    public String getName() {
        return new String("JDBC Technology Adapter");
    }

    @Override
    public String getLocalizationDirectory() {
        return "FlexoLocalization/JDBCTechnologyAdapter";
    }

    @Override
    public JDBCTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
        return new JDBCTechnologyContextManager(this, service);
    }

    @Override
    public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
        // TODO Auto-generated method stub
        return false;
    }
}
