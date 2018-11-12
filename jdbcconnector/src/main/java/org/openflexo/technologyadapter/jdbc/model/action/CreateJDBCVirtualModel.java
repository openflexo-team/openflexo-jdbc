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

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AbstractCreateNatureSpecificVirtualModel;
import org.openflexo.foundation.fml.action.AddUseDeclaration;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.PropertyEntry;
import org.openflexo.foundation.fml.action.PropertyEntry.PropertyType;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.task.Progress;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.dbtype.JDBCDbType;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnColumnRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnInitializer;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.SaveHbnObject;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.action.CreateJDBCVirtualModel.TableMapping.ColumnMapping;

public class CreateJDBCVirtualModel extends AbstractCreateNatureSpecificVirtualModel<CreateJDBCVirtualModel>
		implements TechnologySpecificFlexoAction<JDBCTechnologyAdapter> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateJDBCVirtualModel.class.getPackage().getName());

	public static FlexoActionFactory<CreateJDBCVirtualModel, FlexoObject, FMLObject> actionType = new FlexoActionFactory<CreateJDBCVirtualModel, FlexoObject, FMLObject>(
			"create_jdbc_virtual_model", FlexoActionFactory.newVirtualModelMenu, FlexoActionFactory.defaultGroup,
			FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateJDBCVirtualModel makeNewAction(FlexoObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateJDBCVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FMLObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateJDBCVirtualModel.actionType, VirtualModel.class);
		FlexoObjectImpl.addActionForClass(CreateJDBCVirtualModel.actionType, RepositoryFolder.class);
	}

	private VirtualModel newVirtualModel;

	private JDBCDbType dbType;
	private String address;
	private String user;
	private String password;

	private JDBCConnection jdbcConnection = null;
	private List<JDBCTable> tablesToBeReflected;

	private CreateJDBCVirtualModel(FlexoObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public JDBCTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
	}

	@Override
	public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
		return JDBCTechnologyAdapter.class;
	}

	@Override
	protected void performCreateBehaviours() {
		setDefineSomeBehaviours(true);

		super.performCreateBehaviours();

		CreateFlexoBehaviour createHbnInitializer = CreateFlexoBehaviour.actionType.makeNewEmbeddedAction(getNewFlexoConcept(), null, this);
		createHbnInitializer.setFlexoBehaviourName("initialize");
		createHbnInitializer.setFlexoBehaviourClass(HbnInitializer.class);
		createHbnInitializer.doAction();
		// Unused HbnInitializer initializer = (HbnInitializer)
		createHbnInitializer.getNewFlexoBehaviour();
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Progress.progress(getLocales().localizedForKey("create_virtual_model"));

		try {
			VirtualModelResource vmResource = makeVirtualModelResource();
			newVirtualModel = vmResource.getLoadedResourceData();
			newVirtualModel.setDescription(getNewVirtualModelDescription());
			newVirtualModel.setAbstract(true);
			newVirtualModel.setModelSlotNatureClass(HbnModelSlot.class);
		} catch (SaveResourceException e) {
			throw new SaveResourceException(null);
		} catch (ModelDefinitionException e) {
			throw new FlexoException(e);
		}

		AddUseDeclaration useDeclarationAction = AddUseDeclaration.actionType.makeNewEmbeddedAction(newVirtualModel, null, this);
		useDeclarationAction.setModelSlotClass(HbnModelSlot.class);
		useDeclarationAction.doAction();

		performSetParentConcepts();
		performCreateProperties();
		performCreateBehaviours();
		performCreateInspectors();
		performPostProcessings();

		for (TableMapping tableMapping : getTableMappings()) {
			CreateFlexoConcept createConceptAction = CreateFlexoConcept.actionType.makeNewEmbeddedAction(newVirtualModel, null, this);
			createConceptAction.setNewFlexoConceptName(tableMapping.getConceptName());
			createConceptAction.setNewFlexoConceptDescription("Mapping for table " + tableMapping.getTable().getName());
			for (ColumnMapping columnMapping : tableMapping.getColumnMappings()) {
				PropertyEntry propertyEntry = createConceptAction.newPropertyEntry();
				propertyEntry.setName(columnMapping.getPropertyName());
				switch (columnMapping.getMappingType()) {
					case Primitive:
						propertyEntry.setPropertyType(PropertyType.TECHNOLOGY_ROLE);
						propertyEntry.setFlexoRoleClass((Class) HbnColumnRole.class);
						propertyEntry.setType(columnMapping.getColumn().getDataType().getJavaType());
						System.out.println("Property " + propertyEntry + " type=" + propertyEntry.getType());
						break;
					case ForeignKey:
						propertyEntry.setPropertyType(PropertyType.TECHNOLOGY_ROLE);
						propertyEntry.setFlexoRoleClass(HbnToOneReferenceRole.class);
						propertyEntry.setType(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE);
						for (TableMapping oppositeTableMapping : getTableMappings()) {
							if (oppositeTableMapping.getTable().getName().equals(columnMapping.getOppositeTable().getName())) {
								if (oppositeTableMapping.getConcept() != null) {
									propertyEntry.setType(oppositeTableMapping.getConcept().getInstanceType());
								}
								break;
							}
						}
						break;
					default:
						// TODO
				}
			}

			// createConceptAction.setDefineSomeBehaviours(true);
			// createConceptAction.setDefineDefaultCreationScheme(true);

			createConceptAction.setDefineInspector(false);
			createConceptAction.doAction();

			tableMapping.concept = createConceptAction.getNewFlexoConcept();
			tableMapping.concept.setAbstract(true);

			for (ColumnMapping columnMapping : tableMapping.getColumnMappings()) {
				columnMapping.property = tableMapping.concept.getDeclaredProperty(columnMapping.getPropertyName());
				switch (columnMapping.getMappingType()) {
					case Primitive:
						HbnColumnRole<?> columnRole = (HbnColumnRole<?>) columnMapping.property;
						columnRole.setColumnName(columnMapping.getColumnName());
						columnRole.setDataType(columnMapping.getColumn().getDataType());
						break;
					case ForeignKey:
						HbnToOneReferenceRole toOneReferenceRole = (HbnToOneReferenceRole) columnMapping.property;
						toOneReferenceRole.setColumnName(columnMapping.getColumnName());
						break;
					default:
						// TODO
				}
				if (columnMapping.getColumn().isPrimaryKey()) {
					tableMapping.concept.addToKeyProperties(columnMapping.property);
				}
			}

			createConceptAction.setDefineInspector(true);
			createConceptAction.performCreateInspectors();

			// Create a default creation scheme
			CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewEmbeddedAction(tableMapping.concept, null,
					this);
			createCreationScheme.setFlexoBehaviourName("create");
			createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
			createCreationScheme.doAction();
			CreationScheme creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

			// Add SaveHbnObject to creation scheme
			CreateEditionAction createSaveAction = CreateEditionAction.actionType.makeNewEmbeddedAction(creationScheme.getControlGraph(),
					null, this);
			createSaveAction.setEditionActionClass(SaveHbnObject.class);
			createSaveAction.doAction();
			SaveHbnObject saveHbnObject = (SaveHbnObject) createSaveAction.getBaseEditionAction();
			saveHbnObject.setReceiver(new DataBinding<HbnVirtualModelInstance>("this.container"));
			saveHbnObject.setObject(new DataBinding<FlexoConceptInstance>("this"));
		}

		for (TableMapping tableMapping : getTableMappings()) {
			for (ColumnMapping columnMapping : tableMapping.getColumnMappings()) {
				FlexoProperty<?> property = tableMapping.concept.getDeclaredProperty(columnMapping.getPropertyName());
				if (property instanceof HbnToOneReferenceRole) {
					((HbnToOneReferenceRole) property).setForeignKeyAttributeName(columnMapping.getPropertyName());
					((HbnToOneReferenceRole) property).setVirtualModelInstance(new DataBinding<>("container"));
					System.out.println("Maintenant on cherche " + columnMapping.getOppositeTable().getName());
					for (TableMapping oppositeTableMapping : getTableMappings()) {
						if (oppositeTableMapping.getTable().getName().equals(columnMapping.getOppositeTable().getName())) {
							System.out.println("opposite concept name: " + columnMapping.getOppositeTable().getName());
							System.out.println("opposite concept: " + oppositeTableMapping.getConcept());
							((HbnToOneReferenceRole) property).setFlexoConceptType(oppositeTableMapping.getConcept());
							break;
						}
					}

				}

			}
		}

		newVirtualModel.getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
		newVirtualModel.getResource().getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
	}

	@Override
	public VirtualModel getNewVirtualModel() {
		return newVirtualModel;
	}

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

	public JDBCConnection getJDBCConnection() {
		if (jdbcConnection == null) {
			JDBCFactory factory;
			try {
				factory = new JDBCFactory();
				jdbcConnection = factory.makeNewModel(getDbType(), getAddress(), getUser(), getPassword());
				tablesToBeReflected = new ArrayList<>();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				return null;
			}
		}
		return jdbcConnection;
	}

	public List<JDBCTable> getTablesToBeReflected() {
		// Obtain the JDBCConnection
		if (jdbcConnection == null) {
			getJDBCConnection();
		}
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
			private String columnName;
			private FlexoProperty<?> property;
			private ColumnPropertyMappingType mappingType;
			private boolean selectIt;
			private boolean isPrimaryKey;
			private JDBCTable oppositeTable;

			public ColumnMapping(JDBCColumn column) {
				this.column = column;
				columnName = column.getName();
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

			public String getColumnName() {
				return columnName;
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
