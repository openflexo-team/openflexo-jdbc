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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.rt.error.FMLTechnologyError;
import org.openflexo.foundation.fml.rt.error.FMLResolutionError;
import org.openflexo.foundation.fml.rt.error.FMLNullReferenceError;
import org.openflexo.foundation.fml.rt.error.FMLEvaluation;
import org.openflexo.foundation.fml.rt.error.FMLExecutionException;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.AddJDBCLine.AddJDBCLineImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

@ModelEntity
@ImplementationClass(AddJDBCLineImpl.class)
@FML("AddJDBCLine")
public interface AddJDBCLine extends TechnologySpecificActionDefiningReceiver<JDBCModelSlot, JDBCConnection, JDBCLine> {

	@PropertyIdentifier(type = DataBinding.class)
	String TABLE_KEY = "table";
	String VALUES_KEY = "values";

	@Getter(TABLE_KEY)
	DataBinding<JDBCTable> getTable();

	@Setter(TABLE_KEY)
	void setTable(DataBinding<JDBCTable> table);

	@Getter(value = VALUES_KEY, cardinality = Cardinality.LIST)
	List<AddJDBCLineValue> getValues();

	@Adder(VALUES_KEY)
	void addValue(AddJDBCLineValue value);

	@Remover(VALUES_KEY)
	void removeValue(AddJDBCLineValue value);

	void newValue();

	abstract class AddJDBCLineImpl extends TechnologySpecificActionDefiningReceiverImpl<JDBCModelSlot, JDBCConnection, JDBCLine>
			implements AddJDBCLine {

		private static final Logger logger = Logger.getLogger(AddJDBCLine.class.getPackage().getName());

		private DataBinding<JDBCTable> table;

		@Override
		public JDBCLine execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

			JDBCTable table = FMLEvaluation.evaluate(getTable(), evaluationContext, this);
			if (table == null) {
				throw new FMLNullReferenceError("No JDBC table to insert the line in");
			}
			List<AddJDBCLineValue> values = getValues();
			JDBCFactory factory = SQLHelper.getFactory(table.getSchema().getModel());
			JDBCLine line = factory.newInstance(JDBCLine.class);
			List<JDBCValue> jdbcValues = new ArrayList<>();
			for (AddJDBCLineValue value : values) {
				String name = value.getColumnName();
				String columnValue = FMLEvaluation.evaluate(value.getValue(), evaluationContext, this);
				if (name != null && columnValue != null) {
					JDBCColumn column = table.getColumn(name);
					if (column == null) {
						throw new FMLResolutionError("No column " + name + " in the JDBC table " + table.getName());
					}
					jdbcValues.add(factory.newInstance(JDBCValue.class, line, column, columnValue));
				}
			}
			if (jdbcValues.isEmpty()) {
				throw new FMLTechnologyError("No value to insert in the JDBC table " + table.getName() + " (" + values + ")");
			}
			line.init(null, jdbcValues);
			table.insert(line);
			return line;
		}

		@Override
		public DataBinding<JDBCTable> getTable() {
			if (table == null) {
				table = new DataBinding<>(this, JDBCTable.class, DataBinding.BindingDefinitionType.GET);
				table.setBindingName("table");
			}
			return table;
		}

		@Override
		public void setTable(DataBinding<JDBCTable> table) {
			if (table != null) {
				table.setOwner(this);
				table.setDeclaredType(JDBCTable.class);
				table.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				table.setBindingName("table");
			}
			this.table = table;
		}

		@Override
		public void newValue() {
			AddJDBCLineValue addJDBCLineValue = getFMLModelFactory().newInstance(AddJDBCLineValue.class);
			addJDBCLineValue.setOwner(this);
			addJDBCLineValue.setColumnName("column");
			addJDBCLineValue.setValue(new DataBinding<String>("value"));
			addValue(addJDBCLineValue);
		}

		@Override
		public Type getAssignableType() {
			return JDBCLine.class;
		}
	}
}
