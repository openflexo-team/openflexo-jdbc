/*
 * (c) Copyright 2013 Openflexo
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

import org.openflexo.foundation.resource.FileFlexoIODelegate;
import org.openflexo.foundation.resource.FileFlexoIODelegate.FileFlexoIODelegateImpl;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyContextManager;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCModel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

// extends PamelaResourceImpl<Diagram, DiagramFactory>
// extends FlexoResourceImpl<JDBCModel>

public abstract class JDBCResourceImpl implements JDBCResource {
    
    private static final Logger LOGGER = Logger.getLogger(JDBCResourceImpl.class.getPackage().getName());

	private static ModelFactory MODEL_FACTORY;

	static {
		try {
		    // TODO Factory should be non static
			MODEL_FACTORY = new JDBCFactory(null, null);
		} catch (final ModelDefinitionException e) {
			final String msg = "Error while initializing JDBC model resource";
			LOGGER.log(Level.SEVERE, msg, e);
		}
	}

    public static JDBCResource makeJDBCResource(String modelURI, File modelFile, JDBCTechnologyContextManager technologyContextManager) {
        try {
        	ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(JDBCResource.class,FileFlexoIODelegate.class));
            JDBCResourceImpl returned = (JDBCResourceImpl) factory.newInstance(JDBCResource.class);
            // TODO correct this in the archetype for 1.8.0
            returned.initName(modelFile.getName());
            returned.setFlexoIODelegate(FileFlexoIODelegateImpl.makeFileFlexoIODelegate(modelFile, factory));

            returned.setURI(modelURI);
            returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
            returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
            returned.setTechnologyContextManager(technologyContextManager);
            technologyContextManager.registerResource(returned);

            return returned;
        } catch (ModelDefinitionException e) {
            final String msg = "Error while initializing JDBC model resource";
            LOGGER.log(Level.SEVERE, msg, e);
        }
        return null;
    }

    public static JDBCResource retrieveJDBCResource(File modelFile, JDBCTechnologyContextManager technologyContextManager) {
        try {
        	ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(JDBCResource.class,FileFlexoIODelegate.class));
        	JDBCResourceImpl returned = (JDBCResourceImpl) factory.newInstance(JDBCResource.class);
            // TODO correct this in the archetype for 1.8.0
            returned.initName(modelFile.getName());
            returned.setFlexoIODelegate(FileFlexoIODelegateImpl.makeFileFlexoIODelegate(modelFile, factory));
            returned.setURI(modelFile.toURI().toString());
            returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
            returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
            returned.setTechnologyContextManager(technologyContextManager);
            technologyContextManager.registerResource(returned);
            return returned;
        } catch (ModelDefinitionException e) {
            final String msg = "Error while initializing JDBC model resource";
        	LOGGER.log(Level.SEVERE, msg, e);
        }
        return null;
    }

	@Override
	public JDBCTechnologyAdapter getTechnologyAdapter() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
		}
		return null;
	}

    @Override
    public Class<JDBCModel> getResourceDataClass() {
        return JDBCModel.class;
    }
}
