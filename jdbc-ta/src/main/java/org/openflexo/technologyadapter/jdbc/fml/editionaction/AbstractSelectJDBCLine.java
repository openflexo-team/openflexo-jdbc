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

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * A generic {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link JDBCLine} matching some conditions and a given
 * type.<br>
 * 
 * @author sylvain
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractSelectJDBCLine.AbstractSelectJDBCLineImpl.class)
public interface AbstractSelectJDBCLine<AT> extends AbstractFetchRequest<JDBCModelSlot, JDBCConnection, JDBCLine, AT> {

	@PropertyIdentifier(type = DataBinding.class)
	String TABLE_KEY = "table";

	@Getter(TABLE_KEY)
	@XMLAttribute
	DataBinding<JDBCTable> getTable();

	@Setter(TABLE_KEY)
	void setTable(DataBinding<JDBCTable> table);

	abstract class AbstractSelectJDBCLineImpl<AT> extends AbstractFetchRequestImpl<JDBCModelSlot, JDBCConnection, JDBCLine, AT>
			implements AbstractSelectJDBCLine<AT> {

		private static final Logger logger = Logger.getLogger(AbstractSelectJDBCLine.class.getPackage().getName());

		private DataBinding<JDBCTable> table;

		@Override
		public Type getFetchedType() {
			return JDBCLine.class;
		}

		@Override
		public List<JDBCLine> performExecute(RunTimeEvaluationContext evaluationContext) {

			List<JDBCLine> lines = new ArrayList<>();
			JDBCTable table;
			try {
				table = getTable().getBindingValue(evaluationContext);

				if (table != null) {
					// TODO selectAll isn't a good idea, must have a selection first
					lines.addAll(table.selectAll().getLines());
				}
			} catch (TypeMismatchException | NullReferenceException | ReflectiveOperationException e) {
				logger.log(Level.WARNING, "Can't evaluate table", e);
			}

			return filterWithConditions(lines, evaluationContext);

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
	}
}
