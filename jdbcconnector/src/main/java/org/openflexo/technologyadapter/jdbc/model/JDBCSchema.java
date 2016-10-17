package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
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
	String TABLES = "tables";

	@Initializer
	void init(@Parameter(MODEL) JDBCConnection model);

	@Getter(MODEL)
	JDBCConnection getModel();

	@Getter(value = TABLES, cardinality = Getter.Cardinality.LIST)
	List<JDBCTable> getTables();

	@Setter(TABLES)
	void setTables(List<JDBCTable> tables);

	@Adder(TABLES)
	void addToTables(JDBCTable table);

	@Remover(TABLES)
	void removeFromTables(List<JDBCTable> table);

	@Finder(collection = TABLES, attribute = JDBCTable.NAME)
	JDBCTable getTable(String name);


	abstract class JDBCSchemaImpl implements JDBCSchema {

		@Override
		public List<JDBCTable> getTables() {
			List<JDBCTable> tables = (List<JDBCTable>) performSuperGetter(TABLES);
			if (tables == null) {
				try {
					JDBCConnection model = getModel();
					tables = SQLHelper.getTables(this, SQLHelper.getFactory(model));
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
