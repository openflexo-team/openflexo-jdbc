package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
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

	@Finder(collection = TABLES, attribute = JDBCTable.NAME)
	JDBCTable getTable(String name);

	@Adder(TABLES)
	void addTable(JDBCTable table);

	@Remover(TABLES)
	void removeTable(JDBCTable table);

	/**
	 * Creates a table in the schema and the linked database.
	 *
	 * @param tableName new table name, must be uppercase.
	 * @param attributes list of column attributes
	 * @return true if the database has been created, false otherwise (SQL problem, already exists or incorrect name).
	 */
	boolean createTable(String tableName, String[] ... attributes);

	boolean dropTable(String tableName);

	abstract class JDBCSchemaImpl implements JDBCSchema {

		@Override
		public List<JDBCTable> getTables() {
			List<JDBCTable> tables = (List<JDBCTable>) performSuperGetter(TABLES);
			if (tables == null) {
				try {
					tables = SQLHelper.getTables(this, SQLHelper.getFactory(getModel()));
				} catch (SQLException e) {
					tables = null;
				}
			}
			return tables;
		}

		@Override
		public boolean createTable(String tableName, String[] ... attributes) {
			if (!SQLHelper.isUpperCase(tableName)) return false;

			try {
				JDBCTable table = SQLHelper.createTable(this, SQLHelper.getFactory(getModel()), tableName, attributes);
				addTable(table);
				return true;
			} catch (SQLException e) {
				return false;
			}
		}

		@Override
		public boolean dropTable(String tableName) {
			JDBCTable table = getTable(tableName);
			if (table != null) {
				try {
					SQLHelper.dropTable(this, tableName);
					removeTable(table);
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
			return false;
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
