/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

/**
 * Abstract class for all JDBC Roles
 */
public abstract class JDBCRole<T> extends FlexoRole.FlexoRoleImpl<T> {

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
		final AbstractVirtualModelInstanceModelFactory<?> factory = epi.getFactory();
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
