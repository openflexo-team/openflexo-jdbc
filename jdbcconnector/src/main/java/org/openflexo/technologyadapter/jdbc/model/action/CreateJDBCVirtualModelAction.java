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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.ExpressionProperty;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.SynchronizationScheme;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.controlgraph.IterationAction;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.inspector.InspectorEntry;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.editionaction.CreateFlexoConceptInstanceParameter;
import org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.editionaction.MatchingCriteria;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.fml.JDBCLineRole;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.CreateJDBCResource;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.SelectJDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;

public class CreateJDBCVirtualModelAction extends FlexoAction<CreateJDBCVirtualModelAction, ViewPoint, FMLObject> {

	private static final Logger logger = Logger.getLogger(CreateJDBCVirtualModelAction.class.getPackage().getName());

	public static FlexoActionType<CreateJDBCVirtualModelAction, ViewPoint, FMLObject> actionType = new FlexoActionType<CreateJDBCVirtualModelAction, ViewPoint, FMLObject>(
			"create_jdbc_virtualmodel", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJDBCVirtualModelAction makeNewAction(ViewPoint focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateJDBCVirtualModelAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewPoint object, Vector<FMLObject> globalSelection) {
			// TODO check what should be done
			//return object != null && object.getResourceRepository() instanceof JDBCResourceRepository;
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewPoint object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJDBCVirtualModelAction.actionType, ViewPoint.class);
	}

	private String address = "jdbc:hsqldb:hsql://localhost/";
	private String user = "SA";
	private String password = "";

	private String virtualModelName = "Data";

	private boolean generateSynchronizationScheme = false;

	private Map<JDBCTable, FlexoConcept> tableToConcepts = new HashMap<>();

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

	CreateJDBCVirtualModelAction(ViewPoint focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
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

			ViewPoint viewPoint = getFocusedObject();
			ViewPointResource viewPointResource = (ViewPointResource) viewPoint.getResource();


			FMLTechnologyAdapter fmlTechnologyAdapter = getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
			VirtualModelResourceFactory resourceFactory = fmlTechnologyAdapter.getViewPointResourceFactory().getVirtualModelResourceFactory();

			// creates the virtual model
			VirtualModelResource vmResource = resourceFactory.makeVirtualModelResource(
					getVirtualModelName(),
					viewPointResource,
					fmlTechnologyAdapter.getTechnologyContextManager(),
					true
				);

			VirtualModel virtualModel = vmResource.getLoadedResourceData();
			virtualModel.setDescription("This virtual model was generated to represent the database '" + getAddress() + "'");

			FMLModelFactory fmlFactory = virtualModel.getFMLModelFactory();

			// Creates the db slot
			JDBCModelSlot dbSlot = fmlFactory.newInstance(JDBCModelSlot.class);
			dbSlot.setName("db");
			dbSlot.setDescription("ModelSlot to the db.");
			virtualModel.addToFlexoProperties(dbSlot);

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
		return factory.makeNewModel(getAddress(), getUser(), getPassword());
	}

	private CreationScheme createVirtualModelCreationScheme(FMLModelFactory fmlFactory) {
		CreationScheme creationScheme = fmlFactory.newCreationScheme();
		creationScheme.setName("create");

		addParameter(fmlFactory, creationScheme, "address", String.class, getAddress());
		addParameter(fmlFactory, creationScheme, "user", String.class, getUser());
		addParameter(fmlFactory, creationScheme, "password",  String.class, null);

		AssignationAction assignation = fmlFactory.newAssignationAction();
		assignation.setAssignation(new DataBinding("db", creationScheme, Void.class, DataBinding.BindingDefinitionType.GET_SET));
		CreateJDBCResource action = fmlFactory.newInstance(CreateJDBCResource.class);
		action.setReceiver(new DataBinding("db", creationScheme, JDBCModelSlot.class, DataBinding.BindingDefinitionType.GET));
		action.setResourceName(new DataBinding("'db_' + virtualModelInstance.name", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setResourceCenter(new DataBinding("resourceCenter", creationScheme, FlexoResourceCenter.class, DataBinding.BindingDefinitionType.GET));
		action.setAddress(new DataBinding("parameters.address", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setUser(new DataBinding("parameters.user", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
		action.setPassword(new DataBinding("parameters.password", creationScheme, String.class, DataBinding.BindingDefinitionType.GET));
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
			} else {
				scheme.getControlGraph().sequentiallyAppend(action);
			}


			SelectJDBCLine select = factory.newInstance(SelectJDBCLine.class);
			action.setIterationAction(select);
			select.setReceiver(new DataBinding("db", scheme, JDBCModelSlot.class, DataBinding.BindingDefinitionType.GET));
			select.setTable(new DataBinding("db.schema.getTable('"+ table.getName() + "')", scheme, JDBCTable.class, DataBinding.BindingDefinitionType.GET));

			MatchFlexoConceptInstance match = factory.newMatchFlexoConceptInstance();
			action.setControlGraph(match);

			match.setReceiver(new DataBinding("virtualModelInstance", scheme, VirtualModelInstance.class, DataBinding.BindingDefinitionType.GET));

			FlexoConcept flexoConcept = tableToConcepts.get(table);
			CreationScheme creationScheme = flexoConcept.getCreationSchemes().get(0);
			for (JDBCColumn column : table.getColumns()) {
				if (column.isPrimaryKey()) {
					String name = column.getName();
					MatchingCriteria criteria = factory.newMatchingCriteria(flexoConcept.getDeclaredProperty(name.toLowerCase()));
					criteria.setValue(new DataBinding("item.getValue('" + name + "').value"));
					match.addToMatchingCriterias(criteria);
				}
			}

			CreateFlexoConceptInstanceParameter parameter = factory.newCreateFlexoConceptInstanceParameter(creationScheme.getParameter("line"));
			parameter.setValue(new DataBinding("item", scheme, JDBCLine.class, DataBinding.BindingDefinitionType.GET));
			parameter.setAction(match);
			match.addToParameters(parameter);

			match.setFlexoConceptType(flexoConcept);
			match.setCreationScheme(creationScheme);

		}
	}

	private void addParameter(FMLModelFactory fmlFactory, CreationScheme creationScheme, String address, Type type, String defaultValue) {
		FlexoBehaviourParameter parameter = fmlFactory.newParameter(creationScheme);
		parameter.setName(address);
		parameter.setType(type);
		if (defaultValue != null) {
			parameter.setDefaultValue(new DataBinding<Object>("'" + defaultValue + "'", creationScheme, null, DataBinding.BindingDefinitionType.GET));
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
			ExpressionProperty property = createExpressionPropertyForColumn(factory, column);
			concept.addToFlexoProperties(property);

			InspectorEntry entry = factory.newInspectorEntry(concept.getInspector());
			entry.setName(column.getName());
			entry.setData(new DataBinding<>(property.getName(), concept, null, DataBinding.BindingDefinitionType.GET));
			entry.setWidget(widgetTypeForColumn(column));
		}

		// Adds creation scheme
		CreationScheme creationScheme = createConceptCreationScheme(factory);
		concept.addToFlexoBehaviours(creationScheme);
	}

	private ExpressionProperty createExpressionPropertyForColumn(FMLModelFactory factory, JDBCColumn column) {
		ExpressionProperty property = factory.newExpressionProperty();
		property.setName(column.getName().toLowerCase());
		property.setExpression(new DataBinding("line.getValue('" + column.getName() + "').value", property, JDBCValue.class, DataBinding.BindingDefinitionType.GET_SET));
		return property;
	}

	private FlexoBehaviourParameter.WidgetType widgetTypeForColumn(JDBCColumn column) {
		String type = column.getType().toLowerCase();
		if (type.contains("char")) {
			return FlexoBehaviourParameter.WidgetType.TEXT_FIELD;
		} else if (type.equals("boolean") || type.equals("bool") || type.equals("bit")) {
			return FlexoBehaviourParameter.WidgetType.CHECKBOX;
		} else if (type.contains("int")) {
			return FlexoBehaviourParameter.WidgetType.INTEGER;
		} else if ( type.startsWith("dec") || type.contains("numeric") || type.contains("real") ||
					type.contains("float") || type.startsWith("double") ) {
			return FlexoBehaviourParameter.WidgetType.FLOAT;
		} else {
			// TODO find adequate field
			/* DATE TIME TIMESTAMP */
			return FlexoBehaviourParameter.WidgetType.TEXT_FIELD;
		}
	}

	private CreationScheme createConceptCreationScheme(FMLModelFactory factory) {
		CreationScheme creationScheme = factory.newCreationScheme();
		creationScheme.setName("create");
		addParameter(factory, creationScheme, "line", JDBCLine.class, null);

		AssignationAction assignation = factory.newAssignationAction();
		assignation.setAssignation(new DataBinding("line", creationScheme, Void.class, DataBinding.BindingDefinitionType.GET_SET));
		ExpressionAction action = factory.newExpressionAction();
		action.setExpression(new DataBinding("parameters.line", creationScheme, JDBCLine.class, DataBinding.BindingDefinitionType.GET));
		assignation.setAssignableAction(action);

		creationScheme.setControlGraph(assignation);
		return creationScheme;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
