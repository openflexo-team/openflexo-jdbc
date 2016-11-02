package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

import java.util.List;

/**
 * One JDBC line
 */
@ModelEntity
@ImplementationClass(JDBCLine.JDBCLineImpl.class)
public interface JDBCLine extends FlexoObject {

	void init(JDBCTable table, List<JDBCValue> values);

	JDBCTable getTable();

	List<JDBCValue> getValues();

	JDBCValue getValue(JDBCColumn column);

	abstract class JDBCLineImpl extends FlexoObjectImpl implements JDBCLine {

		private JDBCTable table;
		private List<JDBCValue> values;

		@Override
		public void init(JDBCTable table, List<JDBCValue> values) {
			this.table = table;
			this.values = values;
		}

		@Override
		public JDBCTable getTable() {
			return table;
		}

		@Override
		public List<JDBCValue> getValues() {
			return values;
		}

		@Override
		public JDBCValue getValue(JDBCColumn column) {
			for (JDBCValue value : values) {
				if (value.getColumn() == column) return value;
			}
			return null;
		}
	}

}
