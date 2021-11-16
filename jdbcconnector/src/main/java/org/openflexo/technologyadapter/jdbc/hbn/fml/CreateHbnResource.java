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

package org.openflexo.technologyadapter.jdbc.hbn.fml;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.action.CreationSchemeAction;
import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.hbn.rm.HbnVirtualModelInstanceResource;
import org.openflexo.technologyadapter.jdbc.hbn.rm.HbnVirtualModelInstanceResourceFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

/**
 * {@link EditionAction} used to create an empty {@link HbnVirtualModelInstance} resource
 * 
 * @author charlie, sylvain
 *
 */
@ModelEntity
@ImplementationClass(CreateHbnResource.CreateHbnResourceImpl.class)
@XMLElement
@FML("CreateHbnResource")
public interface CreateHbnResource extends AbstractCreateResource<HbnModelSlot, HbnVirtualModelInstance, JDBCTechnologyAdapter> {

	@PropertyIdentifier(type = String.class)
	public static final String CREATION_SCHEME_URI_KEY = "creationSchemeURI";
	@PropertyIdentifier(type = CreationScheme.class)
	public static final String CREATION_SCHEME_KEY = "creationScheme";
	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";
	@PropertyIdentifier(type = CreateHbnResourceParameter.class, cardinality = Cardinality.LIST)
	public static final String PARAMETERS_KEY = "parameters";

	@PropertyIdentifier(type = DataBinding.class)
	String CONNECTION = "connection";

	/*@PropertyIdentifier(type = DataBinding.class)
	String DB_TYPE = "dbtype";
	@PropertyIdentifier(type = DataBinding.class)
	String ADDRESS_KEY = "address";
	@PropertyIdentifier(type = DataBinding.class)
	String USER_KEY = "user";
	@PropertyIdentifier(type = DataBinding.class)
	String PASSWORD_KEY = "password";*/

	public CompilationUnitResource getVirtualModelResource();

	public void setVirtualModelResource(CompilationUnitResource virtualModelResource);

	public VirtualModel getVirtualModel();

	public void setVirtualModel(VirtualModel virtualModel);

	@Getter(CONNECTION)
	@XMLAttribute
	DataBinding<JDBCConnection> getConnection();

	@Setter(CONNECTION)
	void setConnection(DataBinding<JDBCConnection> aConnection);

	/*@Getter(DB_TYPE)
	@XMLAttribute
	DataBinding<JDBCDbType> getDbType();
	
	@Setter(DB_TYPE)
	void setDbType(DataBinding<JDBCDbType> aType);
	
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

	@Getter(value = CREATION_SCHEME_URI_KEY)
	@XMLAttribute
	public String _getCreationSchemeURI();

	@Setter(CREATION_SCHEME_URI_KEY)
	public void _setCreationSchemeURI(String creationSchemeURI);

	public CreationScheme getCreationScheme();

	public void setCreationScheme(CreationScheme creationScheme);

	public List<CreationScheme> getAvailableCreationSchemes();

	@Getter(value = PARAMETERS_KEY, cardinality = Cardinality.LIST, inverse = CreateHbnResourceParameter.OWNER_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<CreateHbnResourceParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<CreateHbnResourceParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(CreateHbnResourceParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(CreateHbnResourceParameter aParameter);

	public abstract Class<HbnVirtualModelInstanceResourceFactory> getResourceFactoryClass();

	public abstract String getSuffix();

	abstract class CreateHbnResourceImpl extends AbstractCreateResourceImpl<HbnModelSlot, HbnVirtualModelInstance, JDBCTechnologyAdapter>
			implements CreateHbnResource {

		private DataBinding<JDBCConnection> connection;
		/*private DataBinding<JDBCDbType> dbType;
		private DataBinding<String> address;
		private DataBinding<String> user;
		private DataBinding<String> password;*/

		private VirtualModel virtualModel;
		private CompilationUnitResource virtualModelResource;

		private CreationScheme creationScheme;
		private String _creationSchemeURI;
		private List<CreateHbnResourceParameter> parameters = null;

		@Override
		public Type getAssignableType() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getInstanceType();
			}
			return HbnVirtualModelInstance.class;
		}

		@Override
		public CompilationUnitResource getVirtualModelResource() {
			if (virtualModelResource != null) {
				return virtualModelResource;
			}
			if (getVirtualModel() != null) {
				return getVirtualModel().getCompilationUnitResource();
			}
			return virtualModelResource;
		}

		@Override
		public void setVirtualModelResource(CompilationUnitResource virtualModelResource) {
			if ((virtualModelResource == null && getVirtualModelResource() != null)
					|| (virtualModelResource != null && !virtualModelResource.equals(getVirtualModelResource()))) {
				CompilationUnitResource oldValue = getVirtualModelResource();
				this.virtualModelResource = virtualModelResource;
				setVirtualModel(virtualModelResource != null ? virtualModelResource.getCompilationUnit().getVirtualModel() : null);
				getPropertyChangeSupport().firePropertyChange("virtualModelResource", oldValue, virtualModelResource);
			}
		}

		@Override
		public VirtualModel getVirtualModel() {
			if (getCreationScheme() != null) {
				return (VirtualModel) getCreationScheme().getFlexoConcept();
			}
			if (getAssignedFlexoProperty() instanceof HbnModelSlot) {
				return ((HbnModelSlot) getAssignedFlexoProperty()).getAccessedVirtualModel();
			}
			if (virtualModelResource != null) {
				return virtualModelResource.getCompilationUnit().getVirtualModel();
			}
			return virtualModel;
		}

		@Override
		public void setVirtualModel(VirtualModel aVirtualModel) {
			if (this.virtualModel != aVirtualModel) {
				VirtualModel oldValue = this.virtualModel;
				this.virtualModel = aVirtualModel;
				if (aVirtualModel != null) {
					this.virtualModelResource = aVirtualModel.getCompilationUnitResource();
					getPropertyChangeSupport().firePropertyChange("availableCreationSchemes", null, aVirtualModel.getCreationSchemes());
				}
				if (creationScheme == null || creationScheme.getFlexoConcept() != aVirtualModel) {
					if (aVirtualModel.getCreationSchemes().size() > 0) {
						setCreationScheme(aVirtualModel.getCreationSchemes().get(0));
					}
					else {
						setCreationScheme(null);
					}
				}
				getPropertyChangeSupport().firePropertyChange(CreateHbnResource.VIRTUAL_MODEL_KEY, oldValue, aVirtualModel);
				getPropertyChangeSupport().firePropertyChange("availableCreationSchemes", null, getAvailableCreationSchemes());
			}
		}

		@Override
		public String _getCreationSchemeURI() {
			if (getCreationScheme() != null) {
				return getCreationScheme().getURI();
			}
			return _creationSchemeURI;
		}

		@Override
		public void _setCreationSchemeURI(String uri) {
			if (getVirtualModelLibrary() != null) {
				creationScheme = (CreationScheme) getVirtualModelLibrary().getFlexoBehaviour(uri, true);
			}
			_creationSchemeURI = uri;
		}

		@Override
		public CreationScheme getCreationScheme() {

			if (creationScheme == null && _creationSchemeURI != null && getVirtualModelLibrary() != null) {
				creationScheme = (CreationScheme) getVirtualModelLibrary().getFlexoBehaviour(_creationSchemeURI, true);
				updateParameters();
			}
			if (creationScheme == null && ((FlexoProperty<?>) getAssignedFlexoProperty()) instanceof FlexoConceptInstanceRole) {
				creationScheme = ((FlexoConceptInstanceRole) (FlexoProperty<?>) getAssignedFlexoProperty()).getCreationScheme();
				updateParameters();
			}
			return creationScheme;
		}

		@Override
		public void setCreationScheme(CreationScheme creationScheme) {
			if (this.creationScheme != creationScheme) {
				CreationScheme oldValue = this.creationScheme;
				this.creationScheme = creationScheme;
				if (creationScheme != null) {
					_creationSchemeURI = creationScheme.getURI();
				}
				else {
					_creationSchemeURI = null;
				}
				updateParameters();
				getPropertyChangeSupport().firePropertyChange(CREATION_SCHEME_KEY, oldValue, creationScheme);
				getPropertyChangeSupport().firePropertyChange(VIRTUAL_MODEL_KEY, null, getVirtualModel());
			}
		}

		@Override
		public List<CreationScheme> getAvailableCreationSchemes() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getCreationSchemes();
			}
			return null;
		}

		@Override
		public List<CreateHbnResourceParameter> getParameters() {
			// Comment this because of an infinite loop with updateParameters() method
			if (parameters == null) {
				parameters = new ArrayList<>();
				updateParameters();
			}
			return parameters;
		}

		@Override
		public void setParameters(List<CreateHbnResourceParameter> parameters) {
			this.parameters = parameters;
		}

		@Override
		public void addToParameters(CreateHbnResourceParameter parameter) {
			parameter.setOwner(this);
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			parameters.add(parameter);
		}

		@Override
		public void removeFromParameters(CreateHbnResourceParameter parameter) {
			parameter.setOwner(null);
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			parameters.remove(parameter);
		}

		public CreateHbnResourceParameter getParameter(FlexoBehaviourParameter p) {
			for (CreateHbnResourceParameter addEPParam : getParameters()) {
				if (addEPParam.getParam() == p) {
					return addEPParam;
				}
			}
			return null;
		}

		private void updateParameters() {
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			List<CreateHbnResourceParameter> oldValue = new ArrayList<>(parameters);
			List<CreateHbnResourceParameter> parametersToRemove = new ArrayList<>(parameters);
			if (creationScheme != null) {
				for (FlexoBehaviourParameter p : creationScheme.getParameters()) {
					CreateHbnResourceParameter existingParam = getParameter(p);
					if (existingParam != null) {
						parametersToRemove.remove(existingParam);
					}
					else {
						if (getFMLModelFactory() != null) {
							CreateHbnResourceParameter newParam = getFMLModelFactory().newInstance(CreateHbnResourceParameter.class);
							newParam.setParam(p);
							addToParameters(newParam);
						}
					}
				}
			}
			for (CreateHbnResourceParameter removeThis : parametersToRemove) {
				removeFromParameters(removeThis);
			}
			getPropertyChangeSupport().firePropertyChange(PARAMETERS_KEY, oldValue, parameters);
		}

		@Override
		public HbnVirtualModelInstance execute(RunTimeEvaluationContext evaluationContext) throws FlexoException {

			if (getCreationScheme() == null) {
				throw new InvalidArgumentException("No creation scheme defined");
			}

			try {
				String resourceName = getResourceName(evaluationContext);
				String resourceURI = getResourceURI(evaluationContext);
				FlexoResourceCenter<?> rc = getResourceCenter(evaluationContext);

				System.out.println("Creating HbnVirtualModelInstanceResource");

				HbnVirtualModelInstanceResource newResource = createResource(
						getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class),
						getResourceFactoryClass(), evaluationContext, getSuffix(), true);
				HbnVirtualModelInstance data = newResource.getResourceData();
				data.setVirtualModel(getVirtualModel());

				FlexoProperty<HbnVirtualModelInstance> flexoProperty = getAssignedFlexoProperty();
				if (flexoProperty instanceof HbnModelSlot) {
					// Unused HbnModelSlot hbnModelSlot = (HbnModelSlot) flexoProperty;
					/*String url = null;
					try {
						if (getAddress().isValid()) {
							url = getAddress().getBindingValue(evaluationContext);
						}
						else if (hbnModelSlot.getAddress().isValid()) {
							url = hbnModelSlot.getAddress().getBindingValue(evaluationContext);
						}
					} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
						e.printStackTrace();
					}
					String user = null;
					try {
						if (getUser().isValid()) {
							user = getUser().getBindingValue(evaluationContext);
						}
						else if (hbnModelSlot.getUser().isValid()) {
							user = hbnModelSlot.getUser().getBindingValue(evaluationContext);
						}
					} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
						e.printStackTrace();
					}
					String password = null;
					try {
						if (getPassword().isValid()) {
							password = getPassword().getBindingValue(evaluationContext);
						}
						else if (hbnModelSlot.getPassword().isValid()) {
							password = hbnModelSlot.getPassword().getBindingValue(evaluationContext);
						}
					} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
						e.printStackTrace();
					}
					JDBCDbType dbType = null;
					try {
						if (getDbType().isValid()) {
							dbType = getDbType().getBindingValue(evaluationContext);
						}
						else if (hbnModelSlot.getDbType() != null) {
							dbType = hbnModelSlot.getDbType();
						}
					} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
						e.printStackTrace();
					}*/

					try {
						if (getConnection().isValid()) {
							JDBCConnection connection = getConnection().getBindingValue(evaluationContext);
							data.setJDBCConnectionResource((JDBCResource) connection.getResource());
							System.out.println("Setting connection: " + connection.getAddress());
						}
						else {
							throw new InvalidArgumentException("No valid connection while creating new HbnResource");
						}

					} catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
						e.printStackTrace();
					}

					/*data.setAddress(url);
					data.setUser(user);
					data.setPassword(password);
					data.setDbType(dbType);*/

					// Now we should execute CreationScheme
					System.out.println("Executing FML: " + getCreationScheme().getFMLPrettyPrint());
					CreationSchemeAction creationSchemeAction = new CreationSchemeAction(getCreationScheme(), null, null,
							(FlexoBehaviourAction<?, ?, ?>) evaluationContext);
					creationSchemeAction.initWithFlexoConceptInstance(data);
					for (CreateHbnResourceParameter p : getParameters()) {
						FlexoBehaviourParameter param = p.getParam();
						Object value = p.evaluateParameterValue((FlexoBehaviourAction<?, ?, ?>) evaluationContext);
						// System.out.println("For parameter " + param + " value is " + value);
						if (value != null) {
							creationSchemeAction.setParameterValue(param,
									p.evaluateParameterValue((FlexoBehaviourAction<?, ?, ?>) evaluationContext));
						}
					}

					creationSchemeAction.doAction();

					if (data.getVirtualModel().getFlexoBehaviours(HbnInitializer.class).size() > 0) {
						HbnInitializer initializer = data.getVirtualModel().getFlexoBehaviours(HbnInitializer.class).get(0);
						HbnInitializerAction action = new HbnInitializerAction(initializer, data, null,
								(FlexoBehaviourAction<?, ?, ?>) evaluationContext);
						action.doAction();
					}

				}
				else {
					throw new InvalidArgumentException("HbnResource creation must be affected to a HbnModelSlot");
				}

				return data;
			} catch (ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
				throw new FlexoException(e);
			}

		}

		@Override
		public DataBinding<JDBCConnection> getConnection() {
			if (connection == null) {
				connection = new DataBinding<>(this, JDBCConnection.class, DataBinding.BindingDefinitionType.GET);
				connection.setBindingName("connection");
			}
			return connection;
		}

		@Override
		public void setConnection(DataBinding<JDBCConnection> aJDBCConnection) {
			if (aJDBCConnection != null) {
				aJDBCConnection.setOwner(this);
				aJDBCConnection.setDeclaredType(JDBCConnection.class);
				aJDBCConnection.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				aJDBCConnection.setBindingName("connection");
			}
			this.connection = aJDBCConnection;
		}

		/*@Override
		public DataBinding<JDBCDbType> getDbType() {
			if (dbType == null) {
				dbType = new DataBinding<>(this, JDBCDbType.class, DataBinding.BindingDefinitionType.GET);
				dbType.setBindingName("dbtype");
			}
			return dbType;
		}
		
		@Override
		public void setDbType(DataBinding<JDBCDbType> aDbType) {
			if (aDbType != null) {
				aDbType.setOwner(this);
				aDbType.setDeclaredType(JDBCDbType.class);
				aDbType.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				aDbType.setBindingName("dbtype");
			}
			this.dbType = aDbType;
		}
		
		@Override
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

		@Override
		public Class<HbnVirtualModelInstanceResourceFactory> getResourceFactoryClass() {
			return HbnVirtualModelInstanceResourceFactory.class;
		}

		@Override
		public String getSuffix() {
			return HbnVirtualModelInstanceResourceFactory.JDBC_HBN_SUFFIX;
		}

	}

	@DefineValidationRule
	public static class ConnectionIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<CreateHbnResource> {
		public ConnectionIsRequiredAndMustBeValid() {
			super("'connection'_binding_is_required_and_must_be_valid", CreateHbnResource.class);
		}

		@Override
		public DataBinding<JDBCConnection> getBinding(CreateHbnResource object) {
			return object.getConnection();
		}

	}

	@DefineValidationRule
	public static class CreateHbnResourceMustAddressAValidCreationScheme
			extends ValidationRule<CreateHbnResourceMustAddressAValidCreationScheme, CreateHbnResource> {
		public CreateHbnResourceMustAddressAValidCreationScheme() {
			super(CreateHbnResource.class, "create_hbn_resource_must_address_a_valid_creation_scheme");
		}

		@Override
		public ValidationIssue<CreateHbnResourceMustAddressAValidCreationScheme, CreateHbnResource> applyValidation(
				CreateHbnResource action) {
			if (action.getCreationScheme() == null) {
				Vector<FixProposal<CreateHbnResourceMustAddressAValidCreationScheme, CreateHbnResource>> v = new Vector<>();
				if (action.getVirtualModel() != null) {
					for (CreationScheme cs : action.getVirtualModel().getCreationSchemes()) {
						v.add(new SetsCreationScheme(cs));
					}
				}
				return new ValidationError<>(this, action, "create_hbn_resource_does_not_address_a_valid_creation_scheme", v);
			}
			return null;
		}

		protected static class SetsCreationScheme extends FixProposal<CreateHbnResourceMustAddressAValidCreationScheme, CreateHbnResource> {

			private final CreationScheme creationScheme;

			public SetsCreationScheme(CreationScheme creationScheme) {
				super("sets_creation_scheme_to_($creationScheme.name)");
				this.creationScheme = creationScheme;
			}

			public CreationScheme getCreationScheme() {
				return creationScheme;
			}

			@Override
			protected void fixAction() {
				CreateHbnResource action = getValidable();
				action.setCreationScheme(getCreationScheme());
			}

		}
	}

}
