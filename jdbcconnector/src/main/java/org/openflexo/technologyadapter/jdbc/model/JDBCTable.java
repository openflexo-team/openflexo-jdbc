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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC connector table description
 */
@ModelEntity
@ImplementationClass(JDBCTable.JDBCTableImpl.class)
public interface JDBCTable {

    String NAME = "name";
    String SCHEMA = "schema";
    String COLUMNS = "columns";

	@Initializer
	void init(@Parameter(SCHEMA) JDBCSchema schema, @Parameter(NAME) String name);

	@Getter(NAME)
    String getName();

	@Getter(SCHEMA)
    JDBCSchema getSchema();

	@Getter(value = COLUMNS, cardinality = Getter.Cardinality.LIST)
	List<JDBCColumn> getColumns();

	@Finder(collection = COLUMNS, attribute = JDBCColumn.NAME)
	JDBCColumn getColumn(String name);

	/**
	 * Only adds column in the model <b>not</b> in the linked database.
	 */
	@Adder(COLUMNS)
	void addColumn(JDBCColumn table);

	/**
	 * Only removes column from the model <b>not</b> in the linked database.
	 */
	@Remover(COLUMNS)
	void removeColumn(JDBCColumn table);

	/**
	 * Creates a column in the model and the linked database.
	 *
	 * @param columnName new column name, must be uppercase.
	 * @param type column type
	 * @return the created column or null if the creation failed (SQL problem, already exists, incorrect name, ...).
	 */
	JDBCColumn createColumn(String columnName, String type);

	/**
	 * Drops a table in the model and the linked database.
	 * @param columnName column name to drop
	 * @return true if the column has been dropped, false otherwise (SQL problem, doesn't exist, ...).
	 */
	boolean dropColumn(String columnName);

	abstract class JDBCTableImpl implements AccessibleProxyObject, JDBCTable {

		private static final Logger LOGGER = Logger.getLogger(JDBCTable.class.getPackage().getName());

		private boolean columnsInitialized = false;

		@Override
		public List<JDBCColumn> getColumns() {
			List<JDBCColumn> columns = (List<JDBCColumn>) performSuperGetter(COLUMNS);
			if (!columnsInitialized) {
				columnsInitialized = true;
				try {
					JDBCConnection model = getSchema().getModel();
					columns = SQLHelper.getTableColumns(this, SQLHelper.getFactory(model));
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Can't read columns on table '"+ getName()+"'", e);
				}
			}
			return columns;
		}

		@Override
		public JDBCColumn createColumn(String columnName, String type) {
			try {
				JDBCColumn column = SQLHelper.createColumn(this, SQLHelper.getFactory(getSchema().getModel()), columnName, type);
				addColumn(column);
				return column;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't create column '"+ columnName +"' on table '"+ getName()+"'", e);
				return null;
			}
		}

		@Override
		public boolean dropColumn(String columnName) {
			JDBCColumn table = getColumn(columnName);
			if (table != null) {
				try {
					SQLHelper.dropColumn(this, columnName);
					removeColumn(table);
					return true;
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Can't drop column '"+ columnName +"' on table '"+ getName()+"'", e);
					return false;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "[Column] " + getName() + "("+ getColumns().size() +")";
		}
	}
}
