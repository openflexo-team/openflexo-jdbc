package org.openflexo.technologyadapter.jdbc.rm;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.PamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCModel;
import org.openflexo.toolbox.StringUtils;

/**
 *
 */
public class JDBCResourceFactory
    extends PamelaResourceFactory<JDBCResource, JDBCModel, JDBCTechnologyAdapter, JDBCFactory>
{

    protected JDBCResourceFactory() throws ModelDefinitionException {
        super(JDBCResource.class);
    }

    @Override
    public JDBCFactory makeResourceDataFactory(JDBCResource resource, TechnologyContextManager<JDBCTechnologyAdapter> technologyContextManager) throws ModelDefinitionException {
        return new JDBCFactory(resource, technologyContextManager.getServiceManager().getEditingContext()) ;
    }

    @Override
    public JDBCModel makeEmptyResourceData(JDBCResource resource) {
        return resource.getFactory().makeNewModel("", "", "");
    }

    @Override
    public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
        String name = resourceCenter.retrieveName(serializationArtefact);
        return StringUtils.hasExtension(name, ".jdbc");
    }

}
