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

import java.lang.reflect.Type;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFlexoBehaviours;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.rt.FMLRTModelSlot;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.InferedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CreateHbnResource;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnInitializer;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToManyReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnVirtualModelInstanceType;
import org.openflexo.technologyadapter.jdbc.hbn.fml.PerformSQLQuery;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnObjectActorReference;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * An implementation of a {@link ModelSlot} providing basic access to a relational database, based on Hibernate technology<br>
 * 
 * This {@link ModelSlot} is contract-based, as it is configured with a {@link VirtualModel} modelling data beeing accessed through this
 * {@link ModelSlot}. It means that data stored in database is locally reflected as {@link FlexoConceptInstance}s in a
 * {@link VirtualModelInstance} (instance of contract {@link VirtualModel})
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement
@ImplementationClass(HbnModelSlot.HbnModelSlotImpl.class)
@DeclareFlexoRoles({ HbnToOneReferenceRole.class, HbnToManyReferenceRole.class })
@DeclareEditionActions({ CreateHbnResource.class, PerformSQLQuery.class })
@DeclareFlexoBehaviours({ HbnInitializer.class })
@DeclareActorReferences({ HbnObjectActorReference.class })
public interface HbnModelSlot extends InferedFMLRTModelSlot<HbnVirtualModelInstance, JDBCTechnologyAdapter> {

	@PropertyIdentifier(type = DataBinding.class)
	String ADDRESS_KEY = "address";
	@PropertyIdentifier(type = DataBinding.class)
	String USER_KEY = "user";
	@PropertyIdentifier(type = DataBinding.class)
	String PASSWORD_KEY = "password";

	@Getter(ADDRESS_KEY)
	@XMLAttribute
	DataBinding<String> getAddress();

	@Setter(ADDRESS_KEY)
	void setAddress(DataBinding<String> address);

	@Getter(USER_KEY)
	@XMLAttribute
	DataBinding<String> getUser();

	@Setter(USER_KEY)
	void setUser(DataBinding<String> user);

	@Getter(PASSWORD_KEY)
	@XMLAttribute
	DataBinding<String> getPassword();

	@Setter(PASSWORD_KEY)
	void setPassword(DataBinding<String> password);

	abstract class HbnModelSlotImpl extends InferedFMLRTModelSlotImpl<HbnVirtualModelInstance, JDBCTechnologyAdapter>
			implements HbnModelSlot {

		@Override
		public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return super.defaultFlexoRoleName(flexoRoleClass);
		}

		@Override
		public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (JDBCTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		/*@Override
		public ModelSlotInstanceConfiguration<? extends FreeModelSlot<JDBCConnection>, JDBCConnection> createConfiguration(
				FlexoConceptInstance fci, FlexoResourceCenter<?> rc) {
			return new JDBCModelSlotInstanceConfiguration(this, fci, rc);
		}
		
		@Override
		public TechnologyAdapterResource<JDBCConnection, ?> createProjectSpecificEmptyResource(VirtualModelInstance<?, ?> view,
				String filename, String modelUri) {
			// TODO create empty resource
			return null;
		}
		
		@Override
		public TechnologyAdapterResource<JDBCConnection, ?> createSharedEmptyResource(FlexoResourceCenter<?> resourceCenter,
				String relativePath, String filename, String modelUri) {
			// TODO create empty resource
			return null;
		}*/

		@Override
		public ModelSlotInstanceConfiguration<? extends FMLRTModelSlot<HbnVirtualModelInstance, JDBCTechnologyAdapter>, HbnVirtualModelInstance> createConfiguration(
				FlexoConceptInstance fci, FlexoResourceCenter<?> rc) {
			// TODO Auto-generated method stub
			return super.createConfiguration(fci, rc);
		}

		private HbnVirtualModelInstanceType type;

		@Override
		public Type getType() {
			if (type == null || type.getVirtualModel() != getAccessedVirtualModel()) {
				type = HbnVirtualModelInstanceType.getVirtualModelInstanceType(getAccessedVirtualModel());
			}
			return type;
		}

		@Override
		public void setAccessedVirtualModel(VirtualModel aVirtualModel) {
			if (aVirtualModel != getAccessedVirtualModel()) {
				super.setAccessedVirtualModel(aVirtualModel);
				type = HbnVirtualModelInstanceType.getVirtualModelInstanceType(getAccessedVirtualModel());
			}
		}

	}

}
