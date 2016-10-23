package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.factory.AccessibleProxyObject;

import java.util.Collections;
import java.util.List;

/**
 * JDBC result set
 */
@ModelEntity
@ImplementationClass(JDBCResultSet.JDBCResultSetImpl.class)
public interface JDBCResultSet {

	void init(JDBCTable table, List<JDBCLine> lines);

	JDBCTable getTable();

	List<JDBCLine> getLines();

	abstract class JDBCResultSetImpl implements JDBCResultSet, AccessibleProxyObject {

		private JDBCTable table;
		private List<JDBCLine> lines;

		@Override
		public void init(JDBCTable table, List<JDBCLine> lines) {
			this.table = table;
			this.lines = lines;
		}

		@Override
		public List<JDBCLine> getLines() {
			return lines;
		}
	}

	JDBCResultSet empty = new JDBCResultSet() {

		@Override
		public void init(JDBCTable table, List<JDBCLine> lines) {
		}

		@Override
		public JDBCTable getTable() {
			return null;
		}

		@Override
		public List<JDBCLine> getLines() {
			return Collections.emptyList();
		}

		@Override
		public String toString() {
			return "[Result] empty";
		}
	};

}
