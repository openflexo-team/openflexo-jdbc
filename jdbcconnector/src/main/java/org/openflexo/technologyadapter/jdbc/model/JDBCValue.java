package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A value inside a JDBCLine
 */
@ModelEntity
@ImplementationClass(JDBCValue.JDBCValueImpl.class)
public interface JDBCValue extends FlexoObject, InnerResourceData<JDBCConnection> {

	String LINE = "line";
	String COLUMN = "column";
	String VALUE = "value";

	// TODO add **readonly** attribute

	@Initializer
	void init(@Parameter(LINE) JDBCLine line, @Parameter(COLUMN) JDBCColumn column, @Parameter(VALUE) String value);

	@Getter(LINE)
	JDBCLine getLine();

	@Getter(COLUMN)
	JDBCColumn getColumn();

	@Getter(VALUE)
	String getValue();

	int getIntValue();

	@Setter(VALUE)
	boolean setValue(String value);

	abstract class JDBCValueImpl implements JDBCValue, AccessibleProxyObject {
		private static final Logger LOGGER = Logger.getLogger(JDBCTable.class.getPackage().getName());

		@Override
		public boolean setValue(String value) {
			try {
				SQLHelper.update(this, value);
				performSuperSetter(VALUE, value);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't update value on column '"+ getColumn().getTable().getName() + "." + getColumn().getName()+"'", e);
				return false;
			}
		}

		@Override
		public int getIntValue() {
			String value = getValue();
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		@Override
		public JDBCConnection getResourceData() {
			return getLine().getResourceData();
		}

		@Override
		public String toString() {
			StringBuilder columnDescription = new StringBuilder();
			JDBCColumn column = getColumn();
			if (column != null) {
				JDBCTable table = column.getTable();
				if (table != null) {
					columnDescription.append(table.getName());
					columnDescription.append(".");
				}
				columnDescription.append(column.getName());
			} else {
				columnDescription.append("none");
			}
			return "[" + columnDescription + "] " + getValue();
		}
	}
}
