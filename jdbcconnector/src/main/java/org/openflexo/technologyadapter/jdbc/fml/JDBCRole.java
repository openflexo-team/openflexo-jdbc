package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceModelFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

/**
 * Abstract class for all JDBC Roles
 */
public abstract class JDBCRole<T> extends FlexoRole.FlexoRoleImpl<T>implements FlexoRole<T> {

	@Override
	public FlexoRole.RoleCloningStrategy defaultCloningStrategy() {
		return FlexoRole.RoleCloningStrategy.Reference;
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public ActorReference<T> makeActorReference(final T object, final FlexoConceptInstance epi) {
		final VirtualModelInstanceModelFactory factory = (VirtualModelInstanceModelFactory) epi.getFactory();
		final JDBCActorReference<T> returned = factory.newInstance(getActorReferenceClass());
		returned.setFlexoRole(this);
		returned.setFlexoConceptInstance(epi);
		returned.setModellingElement(object);
		return returned;
	}

	public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
	}

	public abstract Class<? extends JDBCActorReference<T>> getActorReferenceClass();

	@Override
	public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
		return JDBCTechnologyAdapter.class;
	}

}
