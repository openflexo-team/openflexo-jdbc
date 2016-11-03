package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

import java.util.List;

/**
 * JDBC result set
 */
@ModelEntity
@ImplementationClass(JDBCResultSet.JDBCResultSetImpl.class)
public interface JDBCResultSet extends FlexoObject, InnerResourceData<JDBCConnection> {

	void init(JDBCTable table, List<JDBCLine> lines);

	JDBCTable getTable();

	List<JDBCLine> getLines();

	abstract class JDBCResultSetImpl extends FlexoObjectImpl implements JDBCResultSet {

		private JDBCTable table;
		private List<JDBCLine> lines;

		@Override
		public void init(JDBCTable table, List<JDBCLine> lines) {
			this.table = table;
			this.lines = lines;
		}

		@Override
		public JDBCTable getTable() {
			return table;
		}

		@Override
		public List<JDBCLine> getLines() {
			return lines;
		}

		@Override
		public JDBCConnection getResourceData() {
			return getTable().getSchema().getModel();
		}
	}

}
