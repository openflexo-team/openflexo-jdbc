package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
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
public interface JDBCResultSetDescription extends FlexoObject, InnerResourceData<JDBCConnection> {

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

	abstract class JDBCResultSetDescriptionImpl implements JDBCResultSetDescription {

		@Override
		public JDBCConnection getResourceData() {
			return getConnection();
		}

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
