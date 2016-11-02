package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC Schema. It contains table description for the connected JDBC resource.
 */
@ModelEntity
@ImplementationClass(JDBCSchema.JDBCSchemaImpl.class)
public interface JDBCSchema extends FlexoObject {

	String MODEL = "model";
	String TABLES = "tables";

	@Initializer
	void init(@Parameter(MODEL) JDBCConnection model);

	@Getter(MODEL)
	JDBCConnection getModel();

	@Getter(value = TABLES, cardinality = Getter.Cardinality.LIST)
	List<JDBCTable> getTables();

	JDBCTable getTable(String name);

	/**
	 * Only adds table in the model <b>not</b> in the linked database.
	 */
	@Adder(TABLES)
	void addTable(JDBCTable table);

	/**
	 * Only removes table from the model <b>not</b> in the linked database.
	 */
	@Remover(TABLES)
	void removeTable(JDBCTable table);

	/**
	 * Creates a table in the model and the linked database.
	 *
	 * @param tableName new table name, must be uppercase.
	 * @param attributes list of column attributes
	 * @return the created table or null if the creation failed (SQL problem, already exists, incorrect name, ...).
	 */
	JDBCTable createTable(String tableName, String[] ... attributes);

	/**
	 * Drops a table in the model and the linked database.
	 * @param table table to drop
	 * @return true if the table has been dropped, false otherwise (SQL problem, doesn't exist, ...).
	 */
	boolean dropTable(JDBCTable table);

	abstract class JDBCSchemaImpl extends FlexoObjectImpl implements JDBCSchema {

		private static final Logger LOGGER = Logger.getLogger(JDBCSchema.class.getPackage().getName());

		/** Internal counter to avoid too many SQL requests */
		private long lastTableUpdate = -1l;

		@Override
		public List<JDBCTable> getTables() {
			List<JDBCTable> tables = (List<JDBCTable>) performSuperGetter(TABLES);
			long currentTimeMillis = System.currentTimeMillis();
			if (lastTableUpdate < currentTimeMillis - 200) {
				try {
					lastTableUpdate = currentTimeMillis;
					SQLHelper.updateTables(this, tables, SQLHelper.getFactory(getModel()));
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Can't read tables on database '" + getModel().getAddress() + "'", e);
				}
			}
			return tables;
		}

		@Override
		public JDBCTable getTable(String name) {
			for (JDBCTable table : getTables()) {
				if (table.getName().equalsIgnoreCase(name)) return table;
			}
			return null;
		}

		@Override
		public JDBCTable createTable(String tableName, String[] ... attributes) {
			try {
				JDBCTable table = SQLHelper.createTable(this, SQLHelper.getFactory(getModel()), tableName, attributes);
				addTable(table);
				return table;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't create table "+ tableName +" on database '"+ getModel().getAddress() +"'", e);
				return null;
			}
		}

		@Override
		public boolean dropTable(JDBCTable table) {
			try {
				SQLHelper.dropTable(this, table.getName());
				removeTable(table);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't drop table "+ table.getName() +" on database '"+ getModel().getAddress() +"'", e);
				return false;
			}
		}

		@Override
		public String toString() {
			StringBuilder text = new StringBuilder("[Schema]");
			int length = text.length();
			for (JDBCTable table : getTables()) {
				if (text.length() > length) text.append(", ");
				text.append(table);
			}
			return text.toString();
		}
	}
}
