package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * JDBC connector table description
 */
@ModelEntity
@ImplementationClass(JDBCTable.JDBCTableImpl.class)
public interface JDBCTable {

    String NAME = "name";
    String SCHEMA = "schema";

	@Initializer
	void init(@Parameter(SCHEMA) JDBCSchema schema, @Parameter(NAME) String name);

	@Getter(NAME)
    String getName();

	@Getter(SCHEMA)
    JDBCSchema getSchema();

    List<JDBCColumn> getColumns();

	JDBCColumn getColumn(String columnId);

	abstract class JDBCTableImpl implements JDBCTable {

		private List<JDBCColumn> columns;

		@Override
		public List<JDBCColumn> getColumns() {
			if (columns == null) {
				try {
					JDBCConnection model = getSchema().getModel();
					columns = SQLHelper.getTableColumns(this, SQLHelper.getFactory(model));
				} catch (SQLException e) {
					columns = null;
				}
			}
			return columns;
		}

		@Override
		public String toString() {
			return "[Table] " + getName() + "("+ getColumns().size() +")";
		}
	}
}
