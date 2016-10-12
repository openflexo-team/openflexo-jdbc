package org.openflexo.technologyadapter.jdbc.rm;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.PamelaResourceFactory;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.toolbox.StringUtils;

/**
 *
 */
public class JDBCResourceFactory
    extends PamelaResourceFactory<JDBCResource, JDBCConnection, JDBCTechnologyAdapter, JDBCFactory>
{

    public static final String JDBC_EXTENSION = ".jdbc";

    public JDBCResourceFactory() throws ModelDefinitionException {
        super(JDBCResource.class);
    }

    @Override
    public JDBCFactory makeResourceDataFactory(JDBCResource resource, TechnologyContextManager<JDBCTechnologyAdapter> technologyContextManager) throws ModelDefinitionException {
        return new JDBCFactory(resource, technologyContextManager.getServiceManager().getEditingContext()) ;
    }

    @Override
    public JDBCConnection makeEmptyResourceData(JDBCResource resource) {
		return resource.getFactory().makeEmptyModel();
	}

    @Override
    public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
        String name = resourceCenter.retrieveName(serializationArtefact);
        return StringUtils.hasExtension(name, JDBC_EXTENSION);
    }

    public <I> JDBCResource makeJDBCResource(String baseName, RepositoryFolder<JDBCResource, I> folder, TechnologyContextManager<JDBCTechnologyAdapter> technologyContextManager)
        throws SaveResourceException, ModelDefinitionException {

        FlexoResourceCenter<I> rc = folder.getResourceRepository().getResourceCenter();
        String artefactName = baseName.endsWith(JDBC_EXTENSION) ? baseName : baseName + JDBC_EXTENSION;
        I serializationArtefact = rc.createEntry(artefactName, folder.getSerializationArtefact());
        JDBCResource newJDBCResource = makeResource(serializationArtefact, rc, technologyContextManager, true);

        return newJDBCResource;
    }
}
