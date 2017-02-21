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

package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription.JDBCResultSetDescriptionImpl;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper.JoinType;

/**
 * A result set description describes the select request that created the result
 */
@ModelEntity @XMLElement
@ImplementationClass(JDBCResultSetDescriptionImpl.class)
public interface JDBCResultSetDescription extends FlexoObject {

	String CONNECTION = "connection";

	String FROM = "from";

	String JOIN_TYPE = "joinType";
	String JOIN = "join";
	String ON = "on";

	String WHERE = "where";
	String ORDER_BY = "orderBy";
	String LIMIT = "limit";
	String OFFSET = "offset";

	@Initializer
	void init(
			@Parameter(CONNECTION) JDBCConnection connection,
			@Parameter(FROM) String from, @Parameter(JOIN_TYPE) String joinType,
			@Parameter(JOIN) String join, @Parameter(ON) String on,
			@Parameter(WHERE) String where, @Parameter(ORDER_BY) String orderBy,
			@Parameter(LIMIT) int limit, @Parameter(OFFSET) int offset
	);

	@Getter(CONNECTION)
	JDBCConnection getConnection();

	@Getter(FROM) @XMLAttribute
	String getFrom();

	@Getter(JOIN_TYPE) @XMLAttribute
	String getJoinType();

	JoinType decodeJoinType();

	@Getter(JOIN) @XMLAttribute
	String getJoin();

	@Getter(ON) @XMLAttribute
	String getOn();

	@Getter(WHERE) @XMLAttribute
	String getWhere();

	@Getter(ORDER_BY) @XMLAttribute
	String getOrderBy();

	@Getter(value = LIMIT, defaultValue = "-1") @XMLAttribute
	int getLimit();

	@Getter(value = OFFSET, defaultValue = "-1") @XMLAttribute
	int getOffset();

	abstract class JDBCResultSetDescriptionImpl extends FlexoObjectImpl implements JDBCResultSetDescription {

		public JoinType decodeJoinType() {
			String joinType = getJoinType();
			if (joinType == null) return JoinType.NoJoin;
			try {
				return JoinType.valueOf(joinType);
			} catch (IllegalArgumentException e) {
				return JoinType.NoJoin;
			}
		}
	}
}
