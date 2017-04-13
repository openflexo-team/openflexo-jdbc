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

package org.openflexo.technologyadapter.jdbc.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificAction;
import org.openflexo.foundation.fml.rt.FreeModelSlotInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCSchema;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

@ModelEntity
@ImplementationClass(AddJDBCTable.AddJDBCTableImpl.class)
@XMLElement
@FML("AddJDBCTable")
public interface AddJDBCTable extends TechnologySpecificAction<JDBCModelSlot, JDBCConnection, JDBCTable> {

	@PropertyIdentifier(type = DataBinding.class)
	String TABLE_NAME = "tableName";

	@Getter(TABLE_NAME)
	@XMLAttribute
	DataBinding<String> getTableName();

	@Setter(TABLE_NAME)
	void setTableName(DataBinding<String> name);

	abstract class AddJDBCTableImpl extends TechnologySpecificAction.TechnologySpecificActionImpl<JDBCModelSlot, JDBCConnection, JDBCTable>
			implements AddJDBCTable {

		private static final Logger logger = Logger.getLogger(AddJDBCTable.class.getPackage().getName());

		private DataBinding<String> tableName;

		@Override
		public Type getAssignableType() {
			return JDBCTable.class;
		}

		@Override
		public JDBCTable execute(RunTimeEvaluationContext evaluationContext) {

			FreeModelSlotInstance<JDBCConnection, JDBCModelSlot> modelSlotInstance = getModelSlotInstance(evaluationContext);
			if (modelSlotInstance.getResourceData() != null) {
				JDBCConnection connection = modelSlotInstance.getAccessedResourceData();
				try {
					if (connection != null) {
						String name = getTableName().getBindingValue(evaluationContext);
						if (name != null) {
							// Create or retrieve this sheet
							return retrieveOrCreateTable(connection, name);
						}
						else {
							logger.warning("Create a JDBC table requires a name");
						}
					}
					else {
						logger.warning("Create a JDBC table requires a JDBC connection");
					}
				} catch (TypeMismatchException | NullReferenceException | InvocationTargetException e) {
					logger.log(Level.WARNING, "Can't create JDBC table", e);
				}

			}
			else {
				logger.warning("Model slot not correctly initialised : model is null");
			}

			return null;

		}

		// Create an Excel Sheet or get the existing one.
		private JDBCTable retrieveOrCreateTable(JDBCConnection connection, String name) {
			JDBCSchema schema = connection.getSchema();
			JDBCTable table = schema.getTable(name);
			if (table == null)
				table = schema.createTable(name);
			return table;
		}

		@Override
		public FreeModelSlotInstance<JDBCConnection, JDBCModelSlot> getModelSlotInstance(RunTimeEvaluationContext evaluationContext) {
			return (FreeModelSlotInstance<JDBCConnection, JDBCModelSlot>) super.getModelSlotInstance(evaluationContext);
		}

		@Override
		public DataBinding<String> getTableName() {
			if (tableName == null) {
				tableName = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
				tableName.setBindingName(TABLE_NAME);
			}
			return tableName;
		}

		@Override
		public void setTableName(DataBinding<String> tableName) {
			if (tableName != null) {
				tableName.setOwner(this);
				tableName.setDeclaredType(String.class);
				tableName.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				tableName.setBindingName(TABLE_NAME);
			}
			this.tableName = tableName;
		}
	}
}
