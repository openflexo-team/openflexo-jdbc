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

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareRepositoryType;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceImpl;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceRepository;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class defines and implements the JDBC technology adapter
 * 
 * @author SomeOne
 * 
 */

@DeclareModelSlots({JDBCModelSlot.class})
@DeclareRepositoryType({JDBCResourceRepository.class })
public class JDBCTechnologyAdapter extends TechnologyAdapter {

    private static String JDBC_FILE_EXTENSION = ".jdbc";

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
    public JDBCTechnologyContextManager getTechnologyContextManager() {
        // TODO Auto-generated method stub
        return (JDBCTechnologyContextManager) super.getTechnologyContextManager();
    }

    @Override
    public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected <I> void performInitializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

        JDBCResourceRepository repository = resourceCenter.getRepository(JDBCResourceRepository.class, this);
        if (repository == null) {
            repository = createNewJDBCRepository(resourceCenter);
        }

        Iterator<I> it = resourceCenter.iterator();

        while (it.hasNext()) {
            I item = it.next();
            if (item instanceof File) {
                this.initializeJDBCFile(resourceCenter, (File) item);
            }
        }

        // Call it to update the current repositories
        notifyRepositoryStructureChanged();
    }

    /**
     * Register file if it is a JDBC file, and
     * reference resource to <code>this</code>
     * 
     * @param resourceCenter
     * @param candidateFile
     */
    private <I> boolean initializeJDBCFile(final FlexoResourceCenter<I> resourceCenter, final File candidateFile) {
        if (!this.isValidJDBCFile(candidateFile)) {
            return false;
        }

        final JDBCResourceImpl jdbcResourceFile = (JDBCResourceImpl) JDBCResourceImpl.retrieveJDBCResource(candidateFile, this.getTechnologyContextManager());
        final JDBCResourceRepository resourceRepository = resourceCenter.getRepository(JDBCResourceRepository.class, this);

        if (jdbcResourceFile != null) {
            try {
                final RepositoryFolder<JDBCResource> folder = resourceRepository.getRepositoryFolder(candidateFile, true);
                resourceRepository.registerResource(jdbcResourceFile, folder);
                this.referenceResource(jdbcResourceFile, resourceCenter);
            } catch (final IOException e) {
                final String msg = "Error during getting JDBC resource folder";
                LOGGER.log(Level.SEVERE, msg, e);
                return false;
            }
        }
        return true;
    } 
    
    /**
     * 
     * @param candidateFile
     * @return true if extension of file match
     *         <code>JDBC_FILE_EXTENSION</code>
     */
    public boolean isValidJDBCFile(final File candidateFile) {
        return candidateFile.getName().endsWith(JDBC_FILE_EXTENSION);
    }


    @Override
    public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <I> boolean contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
        // TODO Auto-generated method stub
        if (contents instanceof File) {
            return initializeJDBCFile(resourceCenter, (File) contents);
        }
        return false;
    }

    @Override
    public <I> boolean contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <I> boolean contentsModified(FlexoResourceCenter<I> resourceCenter, I contents) {
        return false;
    }

    @Override
    public <I> boolean contentsRenamed(FlexoResourceCenter<I> resourceCenter, I contents, String oldName, String newName) {
        return false;
    }

    public JDBCResource createNewJDBCModel(FlexoProject project, String filename, String modelUri) {
        // TODO Auto-generated method stub
        final File file = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
        final JDBCResourceImpl jdbcResourceFile = (JDBCResourceImpl) JDBCResourceImpl.makeJDBCResource(modelUri, file, this.getTechnologyContextManager());
        this.getTechnologyContextManager().registerResource(jdbcResourceFile);
        return jdbcResourceFile;
    }

     /**
     * Create a new JDBCResourceRepository and register it in the given
     * resource center.
     * 
     * @param resourceCenter
     * @return the repository
     */
    private JDBCResourceRepository createNewJDBCRepository(final FlexoResourceCenter<?> resourceCenter) {
        final JDBCResourceRepository repo = new JDBCResourceRepository(this, resourceCenter);
        resourceCenter.registerRepository(repo, JDBCResourceRepository.class, this);
        return repo;
    }

}
