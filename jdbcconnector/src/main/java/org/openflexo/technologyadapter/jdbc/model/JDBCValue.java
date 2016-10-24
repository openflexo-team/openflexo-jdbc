package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;

/**
 * A value inside a JDBCLine
 */
@ModelEntity
@ImplementationClass(JDBCValue.JDBCValueImpl.class)
public interface JDBCValue {

	String LINE = "line";
	String COLUMN = "column";
	String VALUE = "value";

	@Initializer
	void init(@Parameter(LINE) JDBCLine line, @Parameter(COLUMN) JDBCColumn column, @Parameter(VALUE) String value);

	@Getter(LINE)
	JDBCLine getLine();

	@Getter(COLUMN)
	JDBCColumn getColumn();

	@Getter(VALUE)
	String getValue();

	@Setter(VALUE)
	boolean setValue(String value);

	abstract class JDBCValueImpl implements JDBCValue, AccessibleProxyObject {
		@Override
		public boolean setValue(String value) {
			try {
				SQLHelper.update(this, value);
				performSuperSetter(VALUE, value);
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
	}
}
