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
package org.openflexo.technologyadapter.jdbc.model.action;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.ExpressionProperty;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.SynchronizationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.CreateModelSlot;
import org.openflexo.foundation.fml.controlgraph.IterationAction;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.inspector.InspectorEntry;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.editionaction.CreateFlexoConceptInstanceParameter;
import org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.editionaction.MatchingCriteria;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.dbtype.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineRole;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCConnection;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;

public class CreateJDBCMappingVirtualModel extends FlexoAction<CreateJDBCMappingVirtualModel, VirtualModel, FMLObject> {

	private static final Logger logger = Logger.getLogger(CreateJDBCMappingVirtualModel.class.getPackage().getName());

	public static FlexoActionFactory<CreateJDBCMappingVirtualModel, VirtualModel, FMLObject> actionType = new FlexoActionFactory<CreateJDBCMappingVirtualModel, VirtualModel, FMLObject>(
			"create_jdbc_mapping_virtualmodel", FlexoActionFactory.newVirtualModelMenu, FlexoActionFactory.defaultGroup,
			FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJDBCMappingVirtualModel makeNewAction(VirtualModel focusedObject, Vector<FMLObject> globalSelection,
				FlexoEditor editor) {
			return new CreateJDBCMappingVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			// TODO check what should be done
			// return object != null && object.getResourceRepository() instanceof JDBCResourceRepository;
			return true;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJDBCMappingVirtualModel.actionType, VirtualModel.class);
	}

	private JDBCDbType dbType;
	private String address;
	private String user;
	private String password;

	private String virtualModelName = "Data";

	private boolean generateSynchronizationScheme = false;

	private Map<JDBCTable, FlexoConcept> tableToConcepts = new HashMap<>();

	private VirtualModel virtualModel = null;

	public JDBCDbType getDbType() {
		return dbType;
	}

	public void setDbType(JDBCDbType dbType) {
		this.dbType = dbType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVirtualModelName() {
		return virtualModelName;
	}

	public void setVirtualModelName(String virtualModelName) {
		this.virtualModelName = virtualModelName;
	}

	public boolean isGenerateSynchronizationScheme() {
		return generateSynchronizationScheme;
	}

	public void setGenerateSynchronizationScheme(boolean generateSynchronizationScheme) {
		this.generateSynchronizationScheme = generateSynchronizationScheme;
	}

	private CreateJDBCMappingVirtualModel(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			return getTechnologyAdapter().getLocales();
		}
		return super.getLocales();
	}

	private JDBCTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			JDBCConnection connection = createJdbcConnection();
			List<JDBCTable> tables = connection.getSchema().getTables();

			VirtualModel viewPoint = getFocusedObject();
			VirtualModelResource viewPointResource = (VirtualModelResource) viewPoint.getResource();

			FMLTechnologyAdapter fmlTechnologyAdapter = getServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(FMLTechnologyAdapter.class);
			VirtualModelResourceFactory resourceFactory = fmlTechnologyAdapter.getVirtualModelResourceFactory();

			// creates the virtual model
			VirtualModelResource vmResource = resourceFactory.makeContainedVirtualModelResource(getVirtualModelName(), viewPointResource,
					true);

			virtualModel = vmResource.getLoadedResourceData();
			virtualModel.setDescription("This virtual model was generated to represent the database '" + getAddress() + "'");

			// Creates the db slot

			CreateModelSlot action = CreateModelSlot.actionType.makeNewEmbeddedAction(virtualModel, null, this);
			action.setModelSlotName("db");
			action.setTechnologyAdapter(getTechnologyAdapter());
			action.setModelSlotClass(JDBCModelSlot.class);
			action.doAction();
			JDBCModelSlot dbSlot = (JDBCModelSlot) action.getNewModelSlot();
			dbSlot.setName("db");
			dbSlot.setDescription("ModelSlot to the db.");
			virtualModel.addToFlexoProperties(dbSlot);

			FMLModelFactory fmlFactory = virtualModel.getFMLModelFactory();

			// Adds creation scheme
			CreationScheme creationScheme = createVirtualModelCreationScheme(fmlFactory);
			virtualModel.addToFlexoBehaviours(creationScheme);

			// Adds concept for each table
			for (JDBCTable table : tables) {
				addConcept(fmlFactory, virtualModel, table);
			}

			// Adds synchronization scheme
			if (isGenerateSynchronizationScheme()) {
				addVirtualModelSynchronizationScheme(fmlFactory, virtualModel, tables);
			}

			viewPoint.addToVirtualModels(virtualModel);

		} catch (Exception e) {
			throw new FlexoException(e);
		}
	}

	public JDBCConnection createJdbcConnection() throws ModelDefinitionException {
		JDBCFactory factory = new JDBCFactory();
		return factory.makeNewModel(getDbType(), getAddress(), getUser(), getPassword());
	}

	public VirtualModel getVirtualModel() {
		return virtualModel;
	}

	private CreationScheme createVirtualModelCreationScheme(FMLModelFactory fmlFactory) {
		CreationScheme creationScheme = fmlFactory.newCreationScheme();
		creationScheme.setName("create");

		// addParameter(fmlFactory, creationScheme, "dbtype", JDBCDbType.class, getDbType());
		addParameter(fmlFactory, creationScheme, "address", String.class, getAddress());
		addParameter(fmlFactory, creationScheme, "user", String.class, getUser());
		addParameter(fmlFactory, creationScheme, "password", String.class, null);

		AssignationAction<JDBCConnection> assignation = fmlFactory.newAssignationAction();
		assignation.setAssignation(new DataBinding<>("db", creationScheme, Void.class, DataBinding.BindingDefinitionType.GET_SET));
		CreateJDBCConnection action = fmlFactory.newInstance(CreateJDBCConnection.class);
		action.setResourceName(new DataBinding<>("'db_' + this.name", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setResourceCenter(new DataBinding<>("this.resource.resourceCenter", creationScheme, FlexoResourceCenter.class,
				DataBinding.BindingDefinitionType.GET));
		action.setAddress(new DataBinding<>("parameters.address", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setUser(new DataBinding<>("parameters.user", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setPassword(new DataBinding<>("parameters.password", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		// action.setDbType(new DataBinding("parameters.dbtype", creationScheme, JDBCDbType.class, DataBinding.BindingDefinitionType.GET));
		assignation.setAssignableAction(action);

		creationScheme.setControlGraph(assignation);
		return creationScheme;
	}

	private void addVirtualModelSynchronizationScheme(FMLModelFactory factory, VirtualModel virtualModel, List<JDBCTable> tables) {
		SynchronizationScheme scheme = factory.newSynchronizationScheme();
		scheme.setName("synchronizationScheme");
		virtualModel.addToFlexoBehaviours(scheme);

		for (JDBCTable table : tables) {

			IterationAction action = factory.newIterationAction();
			action.setIteratorName("item");

			if (scheme.getControlGraph() == null) {
				scheme.setControlGraph(action);
			}
			else {
				scheme.getControlGraph().sequentiallyAppend(action);
			}

			SelectJDBCLine select = factory.newInstance(SelectJDBCLine.class);
			action.setIterationAction(select);
			select.setReceiver(new DataBinding<>("db", scheme, JDBCModelSlot.class, DataBinding.BindingDefinitionType.GET));
			select.setTable(new DataBinding<>("db.schema.getTable('" + table.getName() + "')", scheme, JDBCTable.class,
					DataBinding.BindingDefinitionType.GET));

			MatchFlexoConceptInstance match = factory.newMatchFlexoConceptInstance();
			action.setControlGraph(match);

			match.setReceiver(new DataBinding<>("this", scheme, FMLRTVirtualModelInstance.class, DataBinding.BindingDefinitionType.GET));

			FlexoConcept flexoConcept = tableToConcepts.get(table);
			CreationScheme creationScheme = flexoConcept.getCreationSchemes().get(0);
			for (JDBCColumn column : table.getColumns()) {
				if (column.isPrimaryKey()) {
					String name = column.getName();
					MatchingCriteria criteria = factory.newMatchingCriteria(flexoConcept.getDeclaredProperty(name.toLowerCase()));
					criteria.setValue(new DataBinding<>("item.getValue('" + name + "').value"));
					match.addToMatchingCriterias(criteria);
				}
			}

			CreateFlexoConceptInstanceParameter parameter = factory
					.newCreateFlexoConceptInstanceParameter(creationScheme.getParameter("line"));
			parameter.setValue(new DataBinding<>("item", scheme, JDBCLine.class, DataBinding.BindingDefinitionType.GET));
			parameter.setAction(match);
			match.addToParameters(parameter);

			match.setFlexoConceptType(flexoConcept);
			match.setCreationScheme(creationScheme);

		}
	}

	private static void addParameter(FMLModelFactory fmlFactory, CreationScheme creationScheme, String address, Type type,
			Object defaultValue) {
		FlexoBehaviourParameter parameter = fmlFactory.newParameter(creationScheme);
		parameter.setName(address);
		parameter.setType(type);
		if (defaultValue != null) {
			parameter.setDefaultValue(
					new DataBinding<>("'" + defaultValue + "'", creationScheme, null, DataBinding.BindingDefinitionType.GET));
		}
		creationScheme.addToParameters(parameter);
	}

	protected void addConcept(FMLModelFactory factory, VirtualModel model, JDBCTable table) {
		FlexoConcept concept = factory.newFlexoConcept();
		model.addToFlexoConcepts(concept);

		tableToConcepts.put(table, concept);

		concept.setName(table.getName());
		concept.setDescription("Generated flexo concept for table " + table.getName());

		// Adds line role
		JDBCLineRole dbRole = factory.newInstance(JDBCLineRole.class);
		dbRole.setName("line");
		dbRole.setDescription("Role linked to the corresponding " + table.getName() + " to the database.");
		concept.addToFlexoProperties(dbRole);

		// Adds expression properties
		for (JDBCColumn column : table.getColumns()) {
			ExpressionProperty<?> property = createExpressionPropertyForColumn(factory, column);
			concept.addToFlexoProperties(property);

			InspectorEntry entry = factory.newInspectorEntry(concept.getInspector());
			entry.setName(column.getName());
			entry.setType(typeForColumn(column));
			entry.setData(new DataBinding<>(property.getName(), concept, null, DataBinding.BindingDefinitionType.GET));
			entry.setIsReadOnly(column.isPrimaryKey());
			entry.setWidget(widgetTypeForColumn(column));
		}

		// Adds creation scheme
		CreationScheme creationScheme = createConceptCreationScheme(factory);
		concept.addToFlexoBehaviours(creationScheme);
	}

	private static ExpressionProperty<?> createExpressionPropertyForColumn(FMLModelFactory factory, JDBCColumn column) {
		ExpressionProperty<?> property = factory.newExpressionProperty();
		property.setName(column.getName().toLowerCase());
		String suffix = typeForColumn(column) == Integer.class ? "intValue" : "value";
		property.setExpression(new DataBinding<>("line.getValue('" + column.getName() + "')." + suffix, property, JDBCValue.class,
				DataBinding.BindingDefinitionType.GET_SET));
		return property;
	}

	private static FlexoBehaviourParameter.WidgetType widgetTypeForColumn(JDBCColumn column) {
		String type = column.getTypeAsString().toLowerCase();
		if (type.contains("char")) {
			return FlexoBehaviourParameter.WidgetType.TEXT_FIELD;
		}
		else if (type.equals("boolean") || type.equals("bool") || type.equals("bit")) {
			return FlexoBehaviourParameter.WidgetType.CHECKBOX;
		}
		else if (type.contains("int")) {
			return FlexoBehaviourParameter.WidgetType.INTEGER;
		}
		else if (type.startsWith("dec") || type.contains("numeric") || type.contains("real") || type.contains("float")
				|| type.startsWith("double")) {
			return FlexoBehaviourParameter.WidgetType.FLOAT;
		}
		else {
			// TODO find adequate field
			/* DATE TIME TIMESTAMP */
			return FlexoBehaviourParameter.WidgetType.TEXT_FIELD;
		}
	}

	private static Type typeForColumn(JDBCColumn column) {
		String type = column.getTypeAsString().toLowerCase();
		if (type.contains("char")) {
			return String.class;
		}
		else if (type.equals("boolean") || type.equals("bool") || type.equals("bit")) {
			return Boolean.class;
		}
		else if (type.contains("int")) {
			return Integer.class;
		}
		else if (type.startsWith("dec") || type.contains("numeric") || type.contains("real") || type.contains("float")
				|| type.startsWith("double")) {
			return Float.class;
		}
		else {
			// TODO find adequate field
			/* DATE TIME TIMESTAMP */
			return String.class;
		}
	}

	private static CreationScheme createConceptCreationScheme(FMLModelFactory factory) {
		CreationScheme creationScheme = factory.newCreationScheme();
		creationScheme.setName("create");
		addParameter(factory, creationScheme, "line", JDBCLine.class, null);

		AssignationAction<JDBCLine> assignation = factory.newAssignationAction();
		assignation.setAssignation(new DataBinding<>("line", creationScheme, Void.class, DataBinding.BindingDefinitionType.GET_SET));
		ExpressionAction<JDBCLine> action = factory.newExpressionAction();
		action.setExpression(new DataBinding<>("parameters.line", creationScheme, JDBCLine.class, DataBinding.BindingDefinitionType.GET));
		assignation.setAssignableAction(action);

		creationScheme.setControlGraph(assignation);
		return creationScheme;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
