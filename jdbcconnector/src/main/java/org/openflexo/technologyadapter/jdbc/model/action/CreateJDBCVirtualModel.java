/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AbstractCreateVirtualModel;
import org.openflexo.foundation.fml.action.AddUseDeclaration;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.PropertyEntry;
import org.openflexo.foundation.fml.action.PropertyEntry.PropertyType;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.task.Progress;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModel.TableMapping.ColumnMapping;
import org.openflexo.toolbox.StringUtils;

public class CreateJDBCVirtualModel extends AbstractCreateVirtualModel<CreateJDBCVirtualModel, VirtualModel, FMLObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateJDBCVirtualModel.class.getPackage().getName());

	public static FlexoActionType<CreateJDBCVirtualModel, VirtualModel, FMLObject> actionType = new FlexoActionType<CreateJDBCVirtualModel, VirtualModel, FMLObject>(
			"create_jdbc_virtual_model", FlexoActionType.newVirtualModelMenu, FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJDBCVirtualModel makeNewAction(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateJDBCVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJDBCVirtualModel.actionType, VirtualModel.class);
	}

	private String newVirtualModelName;
	private String newVirtualModelDescription;
	private VirtualModel newVirtualModel;

	private String address = "jdbc:hsqldb:hsql://localhost/";
	private String user = "SA";
	private String password = "";

	CreateJDBCVirtualModel(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class).getLocales();
		}
		return super.getLocales();
	}

	public JDBCTechnologyAdapter getDiagramTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
	}

	protected DrawingGraphicalRepresentation makePaletteGraphicalRepresentation() throws ModelDefinitionException {
		FGEModelFactory factory = new FGEModelFactoryImpl();
		DrawingGraphicalRepresentation gr = factory.makeDrawingGraphicalRepresentation();
		gr.setDrawWorkingArea(true);
		gr.setWidth(260);
		gr.setHeight(300);
		gr.setIsVisible(true);
		return gr;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {

		Progress.progress(getLocales().localizedForKey("create_virtual_model"));

		FMLTechnologyAdapter fmlTechnologyAdapter = getServiceManager().getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		VirtualModelResourceFactory factory = fmlTechnologyAdapter.getVirtualModelResourceFactory();

		try {
			VirtualModelResource vmResource = factory.makeContainedVirtualModelResource(getNewVirtualModelName(),
					(VirtualModelResource) getFocusedObject().getResource(), fmlTechnologyAdapter.getTechnologyContextManager(), true);
			newVirtualModel = vmResource.getLoadedResourceData();
			newVirtualModel.setDescription(newVirtualModelDescription);
			newVirtualModel.setAbstract(true);
		} catch (SaveResourceException e) {
			throw new SaveResourceException(null);
		} catch (ModelDefinitionException e) {
			throw new FlexoException(e);
		}

		performSetParentConcepts();
		performCreateProperties();
		performCreateBehaviours();
		performCreateInspectors();
		performPostProcessings();

		AddUseDeclaration useDeclarationAction = AddUseDeclaration.actionType.makeNewEmbeddedAction(newVirtualModel, null, this);
		useDeclarationAction.setModelSlotClass(HbnModelSlot.class);
		useDeclarationAction.doAction();

		for (TableMapping tableMapping : getTableMappings()) {
			CreateFlexoConcept createConceptAction = CreateFlexoConcept.actionType.makeNewEmbeddedAction(newVirtualModel, null, this);
			createConceptAction.setNewFlexoConceptName(tableMapping.getConceptName());
			createConceptAction.setNewFlexoConceptDescription("Mapping for table " + tableMapping.getTable().getName());
			for (ColumnMapping columnMapping : tableMapping.getColumnMappings()) {
				PropertyEntry propertyEntry = createConceptAction.newPropertyEntry();
				propertyEntry.setName(columnMapping.getPropertyName());
				switch (columnMapping.getMappingType()) {
					case Primitive:
						propertyEntry.setPropertyType(PropertyType.ABSTRACT_PROPERTY);
						propertyEntry.setType(columnMapping.getColumn().getJavaType());
					default:
						// TODO
				}
			}
			createConceptAction.setDefineInspector(true);
			createConceptAction.doAction();
			tableMapping.concept = createConceptAction.getNewFlexoConcept();
			tableMapping.concept.setAbstract(true);
			for (ColumnMapping columnMapping : tableMapping.getColumnMappings()) {
				columnMapping.property = tableMapping.concept.getDeclaredProperty(columnMapping.getPropertyName());
				if (columnMapping.getColumn().isPrimaryKey()) {
					tableMapping.concept.addToKeyProperties(columnMapping.property);
				}
			}
		}

		newVirtualModel.getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
		newVirtualModel.getResource().getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
	}

	public boolean isNewVirtualModelNameValid() {
		if (StringUtils.isEmpty(newVirtualModelName)) {
			return false;
		}
		if (getFocusedObject().getVirtualModelNamed(newVirtualModelName) != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid() {
		if (!isNewVirtualModelNameValid()) {
			return false;
		}
		return true;
	}

	@Override
	public VirtualModel getNewVirtualModel() {
		return newVirtualModel;
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

	public String getNewVirtualModelName() {
		return newVirtualModelName;
	}

	public void setNewVirtualModelName(String newVirtualModelName) {
		this.newVirtualModelName = newVirtualModelName;

		getPropertyChangeSupport().firePropertyChange("newVirtualModelName", null, newVirtualModelName);

	}

	public String getNewVirtualModelDescription() {
		return newVirtualModelDescription;
	}

	public void setNewVirtualModelDescription(String newVirtualModelDescription) {
		this.newVirtualModelDescription = newVirtualModelDescription;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelDescription", null, newVirtualModelDescription);
	}

	private JDBCConnection jdbcConnection = null;
	private List<JDBCTable> tablesToBeReflected;

	public JDBCConnection getJDBCConnection() {
		if (jdbcConnection == null) {
			JDBCFactory factory;
			try {
				factory = new JDBCFactory();
				jdbcConnection = factory.makeNewModel(getAddress(), getUser(), getPassword());
				tablesToBeReflected = new ArrayList<>();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				return null;
			}
		}
		return jdbcConnection;
	}

	public List<JDBCTable> getTablesToBeReflected() {
		return tablesToBeReflected;
	}

	public List<JDBCTable> getAllTables() {
		if (getJDBCConnection().getConnection() != null && getJDBCConnection().getSchema() != null) {
			return getJDBCConnection().getSchema().getTables();
		}
		return Collections.emptyList();
	}

	private List<TableMapping> tableMappings;

	public List<TableMapping> getTableMappings() {
		if (tableMappings == null) {
			tableMappings = new ArrayList<>();
			for (JDBCTable t : getTablesToBeReflected()) {
				TableMapping tm = new TableMapping(t);
				tableMappings.add(tm);
			}
		}
		return tableMappings;
	}

	public void clearTableMappings() {
		tableMappings.clear();
		tableMappings = null;
	}

	@Override
	public int getExpectedProgressSteps() {
		return 15;
	}

	public enum ColumnPropertyMappingType {
		Primitive, ForeignKey, ManyToMany
	}

	public class TableMapping {

		private JDBCTable table;
		private FlexoConcept concept;
		private String conceptName;

		private List<ColumnMapping> columnMappings;

		public TableMapping(JDBCTable table) {
			this.table = table;
			conceptName = table.getName().substring(0, 1).toUpperCase() + table.getName().substring(1).toLowerCase();
			columnMappings = new ArrayList<>();
			for (JDBCColumn col : table.getColumns()) {
				ColumnMapping colMapping = new ColumnMapping(col);
				columnMappings.add(colMapping);
			}
		}

		public JDBCTable getTable() {
			return table;
		}

		public List<ColumnMapping> getColumnMappings() {
			return columnMappings;
		}

		public String getConceptName() {
			return conceptName;
		}

		public FlexoConcept getConcept() {
			return concept;
		}

		public class ColumnMapping {
			private JDBCColumn column;
			private String propertyName;
			private FlexoProperty<?> property;
			private ColumnPropertyMappingType mappingType;
			private boolean selectIt;
			private boolean isPrimaryKey;
			private JDBCTable oppositeTable;

			public ColumnMapping(JDBCColumn column) {
				this.column = column;
				propertyName = column.getName().toLowerCase();
				mappingType = ColumnPropertyMappingType.Primitive;
				selectIt = true;
				isPrimaryKey = column.isPrimaryKey();
				for (JDBCTable t : getAllTables()) {
					if (t.getName().toUpperCase().equals(column.getName().toUpperCase())) {
						mappingType = ColumnPropertyMappingType.ForeignKey;
						oppositeTable = t;
					}
				}
			}

			public JDBCColumn getColumn() {
				return column;
			}

			public String getPropertyName() {
				return propertyName;
			}

			public boolean isPrimaryKey() {
				return isPrimaryKey;
			}

			public boolean selectIt() {
				return selectIt;
			}

			public void setSelectIt(boolean selectIt) {
				if (selectIt != this.selectIt) {
					this.selectIt = selectIt;
					getPropertyChangeSupport().firePropertyChange("selectIt", !selectIt, selectIt);
				}
			}

			public FlexoProperty<?> getProperty() {
				return property;
			}

			public ColumnPropertyMappingType getMappingType() {
				return mappingType;
			}

			public void setMappingType(ColumnPropertyMappingType mappingType) {
				if ((mappingType == null && this.mappingType != null) || (mappingType != null && !mappingType.equals(this.mappingType))) {
					ColumnPropertyMappingType oldValue = this.mappingType;
					this.mappingType = mappingType;
					getPropertyChangeSupport().firePropertyChange("mappingType", oldValue, mappingType);
				}
			}

			public JDBCTable getOppositeTable() {
				if (getMappingType() == ColumnPropertyMappingType.Primitive) {
					return null;
				}
				return oppositeTable;
			}

			public void setOppositeTable(JDBCTable oppositeTable) {
				if ((oppositeTable == null && this.oppositeTable != null)
						|| (oppositeTable != null && !oppositeTable.equals(this.oppositeTable))) {
					JDBCTable oldValue = this.oppositeTable;
					this.oppositeTable = oppositeTable;
					getPropertyChangeSupport().firePropertyChange("oppositeTable", oldValue, oppositeTable);
				}
			}
		}

	}

}
