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
import java.util.logging.Logger;

import org.openflexo.foundation.fml.editionaction.AbstractFetchRequest;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.jdbc.JDBCModelSlot;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * A generic {@link AbstractFetchRequest} allowing to retrieve a selection of some {@link JDBCTable} matching some conditions and a given
 * type.<br>
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(AbstractSelectJDBCTable.AbstractSelectJDBCTableImpl.class)
public interface AbstractSelectJDBCTable<AT> extends AbstractFetchRequest<JDBCModelSlot, JDBCConnection, JDBCTable, AT> {

	abstract class AbstractSelectJDBCTableImpl<AT> extends AbstractFetchRequestImpl<JDBCModelSlot, JDBCConnection, JDBCTable, AT>
			implements AbstractSelectJDBCTable<AT> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(AbstractSelectJDBCTable.class.getPackage().getName());

		@Override
		public Type getFetchedType() {
			return JDBCTable.class;
		}

		@Override
		public List<JDBCTable> performExecute(RunTimeEvaluationContext evaluationContext) {

			JDBCConnection connection = getReceiver(evaluationContext);
			List<JDBCTable> result = new ArrayList<>(connection.getSchema().getTables());
			return filterWithConditions(result, evaluationContext);
		}
	}
}
