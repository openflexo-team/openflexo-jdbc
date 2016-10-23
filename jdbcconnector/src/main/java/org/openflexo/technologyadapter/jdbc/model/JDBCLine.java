package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.factory.AccessibleProxyObject;

import java.util.List;

/**
 * One JDBC line
 */
@ModelEntity
@ImplementationClass(JDBCLine.JDBCLineImpl.class)
public interface JDBCLine {

	void init(JDBCTable table, List<JDBCValue> values);

	JDBCTable getTable();

	List<JDBCValue> getValues();

	abstract class JDBCLineImpl implements JDBCLine, AccessibleProxyObject {

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
	}

}
