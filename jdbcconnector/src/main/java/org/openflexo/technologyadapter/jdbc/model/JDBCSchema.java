package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * JDBC Schema. It contains table description for the connected JDBC resource.
 */
@ModelEntity
@ImplementationClass(JDBCSchema.JDBCSchemaImpl.class)
public interface JDBCSchema extends AccessibleProxyObject {

	String MODEL = "model";

	@Initializer
	void init(@Parameter(MODEL) JDBCModel model) throws SQLException;

	@Getter(MODEL)
	JDBCModel getModel();

    List<JDBCTable> getTables();

	abstract class JDBCSchemaImpl implements JDBCSchema {

		private List<JDBCTable> tables;

		@Override
		public List<JDBCTable> getTables() {
			if (tables == null) {
				try {
					JDBCModel model = getModel();
					tables = SQLHelper.getTables(this, model.getConnection(), SQLHelper.getFactory(model));
				} catch (SQLException e) {
					tables = null;
				}
			}
			return tables;
		}

		@Override
		public String toString() {
			StringBuilder text = new StringBuilder("[Schema]");
			int length = text.length();
			for (JDBCTable table : getTables()) {
				if (text.length() > length) text.append(", ");
				text.append(table);
			}
			return "[Schema]";
		}
	}
}
