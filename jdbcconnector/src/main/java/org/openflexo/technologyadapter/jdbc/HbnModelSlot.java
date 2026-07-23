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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFlexoBehaviours;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFMLRTModelSlotInstance;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstanceModelFactory;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCConnection;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CommitTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.CreateHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnColumnRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnInitializer;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnOneToManyReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnVirtualModelInstanceType;
import org.openflexo.technologyadapter.jdbc.hbn.fml.OpenTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.PerformSQLQuery;
import org.openflexo.technologyadapter.jdbc.hbn.fml.RefreshHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.fml.RollbackTransaction;
import org.openflexo.technologyadapter.jdbc.hbn.fml.SaveHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnObjectActorReference;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

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
@FML("HbnModelSlot")
@ImplementationClass(HbnModelSlot.HbnModelSlotImpl.class)
@DeclareFlexoRoles({ HbnColumnRole.class, HbnToOneReferenceRole.class, HbnOneToManyReferenceRole.class })
@DeclareEditionActions({ CreateJDBCConnection.class, PerformSQLQuery.class, OpenTransaction.class, CommitTransaction.class,
		RollbackTransaction.class, CreateHbnObject.class, SaveHbnObject.class, RefreshHbnObject.class })
@DeclareFlexoBehaviours({ HbnInitializer.class })
@DeclareActorReferences({ HbnObjectActorReference.class, ReflectedFMLRTModelSlotInstance.class })
public interface HbnModelSlot
		extends ReflectedFMLRTModelSlot<HbnVirtualModelInstance, JDBCResource, JDBCConnection, JDBCTechnologyAdapter> {

	/*@PropertyIdentifier(type = JDBCDbType.class)
	String DB_TYPE = "dbtype";
	@PropertyIdentifier(type = DataBinding.class)
	String ADDRESS_KEY = "address";
	@PropertyIdentifier(type = DataBinding.class)
	String USER_KEY = "user";
	@PropertyIdentifier(type = DataBinding.class)
	String PASSWORD_KEY = "password";
	
	@Getter(DB_TYPE)
	@XMLAttribute
	JDBCDbType getDbType();
	
	@Setter(DB_TYPE)
	void setDbType(JDBCDbType aType);
	
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
	void setPassword(DataBinding<String> password);*/

	abstract class HbnModelSlotImpl
			extends ReflectedFMLRTModelSlotImpl<HbnVirtualModelInstance, JDBCResource, JDBCConnection, JDBCTechnologyAdapter>
			implements HbnModelSlot {

		private static final Logger logger = Logger.getLogger(HbnModelSlotImpl.class.getPackage().getName());

		/*private DataBinding<String> address;
		private DataBinding<String> user;
		private DataBinding<String> password;*/
		private HbnVirtualModelInstanceType type;

		@Override
		public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		/**
		 * Connect this reflected model slot to supplied {@link JDBCResource}: build a {@link HbnVirtualModelInstance} configured with the
		 * annotated contract {@link VirtualModel} (the accessed virtual model), then open the Hibernate session and build the mapping from the
		 * <code>@Table</code>/<code>@Property</code> annotations declared on the contract concepts.
		 *
		 * <p>
		 * Contrary to the exhaustive reflection performed by the XML and Excel model slots at connect time, no {@link FlexoConceptInstance} is
		 * created here: instances are produced lazily, driven by queries (see
		 * {@link org.openflexo.technologyadapter.jdbc.hbn.fml.PerformSQLQuery} and
		 * {@link HbnVirtualModelInstance#getFlexoConceptInstances(org.hibernate.query.Query, FlexoConceptInstance, org.openflexo.foundation.fml.FlexoConcept)}).
		 * </p>
		 */
		@Override
		public ReflectedFMLRTModelSlotInstance<HbnVirtualModelInstance, JDBCResource, JDBCConnection, JDBCTechnologyAdapter> connectTo(
				JDBCResource resource, FlexoConceptInstance context) {

			try {
				HbnVirtualModelInstanceModelFactory factory = new HbnVirtualModelInstanceModelFactory(resource,
						getServiceManager().getEditingContext(), getServiceManager().getTechnologyAdapterService());
				HbnVirtualModelInstance vmi = factory.newInstance(HbnVirtualModelInstance.class);
				vmi.setReflectedModelFactory(factory);
				vmi.setVirtualModel(getAccessedVirtualModel());
				vmi.setReflectedResource(resource);
				vmi.setJDBCConnectionResource(resource);

				// Open the connection and build the Hibernate mapping from the annotated contract VirtualModel.
				// No FlexoConceptInstance is created at this stage (query-driven reflection).
				vmi.connectToDB();

				ReflectedFMLRTModelSlotInstance<HbnVirtualModelInstance, JDBCResource, JDBCConnection, JDBCTechnologyAdapter> modelSlotInstance;
				modelSlotInstance = makeActorReference(vmi, context);
				context.addToActors(modelSlotInstance);
				return modelSlotInstance;

			} catch (ModelDefinitionException e) {
				logger.warning("Unexpected ModelDefinitionException: " + e);
				e.printStackTrace();
				return null;
			} catch (FlexoException e) {
				logger.warning("Could not connect to database: " + e);
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return super.defaultFlexoRoleName(flexoRoleClass);
		}

		@Override
		public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (JDBCTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

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

		/*@Override
		public DataBinding<String> getAddress() {
			if (address == null) {
				address = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				address.setBindingName("address");
			}
			return address;
		}
		
		@Override
		public void setAddress(DataBinding<String> address) {
			if (address != null) {
				address.setOwner(this);
				address.setDeclaredType(String.class);
				address.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				address.setBindingName("address");
			}
			this.address = address;
		}
		
		@Override
		public DataBinding<String> getUser() {
			if (user == null) {
				user = new DataBinding<>(this, String.class, DataBinding.BindingDefinitionType.GET);
				user.setBindingName("user");
			}
			return user;
		}
		
		@Override
		public void setUser(DataBinding<String> user) {
			if (user != null) {
				user.setOwner(this);
				user.setDeclaredType(String.class);
				user.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				user.setBindingName("user");
			}
			this.user = user;
		}
		
		@Override
		public DataBinding<String> getPassword() {
			if (password == null) {
				password = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
				password.setBindingName("password");
			}
			return password;
		}
		
		@Override
		public void setPassword(DataBinding<String> password) {
			if (password != null) {
				password.setOwner(this);
				password.setDeclaredType(String.class);
				password.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				password.setBindingName("password");
			}
			this.password = password;
		}*/

	}

}
