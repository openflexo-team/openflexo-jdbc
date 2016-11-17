package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC result set
 */
@ModelEntity
@ImplementationClass(JDBCResultSet.JDBCResultSetImpl.class)
public interface JDBCResultSet extends FlexoObject, InnerResourceData<JDBCConnection> {

	// TODO add **readonly** attribute

	void init(JDBCConnection connection, JDBCResultSetDescription description, List<JDBCLine> lines);

	JDBCConnection getConnection();

	JDBCResultSetDescription getResultSetDescription();

	List<JDBCLine> getLines();

	JDBCLine find(String ... keys);

	JDBCLine find(List<String> keys);

	abstract class JDBCResultSetImpl extends FlexoObjectImpl implements JDBCResultSet {

		private JDBCConnection connection;
		private JDBCResultSetDescription description;
		private Map<String, JDBCLine> lines = new LinkedHashMap<>();

		@Override
		public void init(JDBCConnection connection, JDBCResultSetDescription description, List<JDBCLine> lines) {
			this.connection = connection;
			this.description = description;
			for (JDBCLine line : lines) {
				this.lines.put(constructHash(line), line);
			}
		}

		private String constructHash(JDBCLine line) {
			StringBuilder hash = new StringBuilder();
			for (JDBCValue value : line.getValues()) {
				if (value.getColumn().isPrimaryKey()) {
					if (hash.length() > 0) hash.append("-");
					hash.append(value.getValue());
				}
			}
			return hash.toString();
		}

		@Override
		public JDBCConnection getConnection() {
			return connection;
		}

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			return description;
		}

		@Override
		public List<JDBCLine> getLines() {
			return new ArrayList<>(lines.values());
		}

		@Override
		public JDBCLine find(String... keys) {
			return find(Arrays.asList(keys));
		}

		@Override
		public JDBCLine find(List<String> keys) {
			StringBuilder hash = new StringBuilder();
			for (String key : keys) {
				if (hash.length() > 0) hash.append("-");
				hash.append(key);
			}
			return lines.get(hash.toString());
		}

		@Override
		public JDBCConnection getResourceData() {
			return getConnection();
		}
	}

}
