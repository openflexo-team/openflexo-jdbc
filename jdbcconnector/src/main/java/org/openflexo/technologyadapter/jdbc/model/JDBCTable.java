package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

import java.sql.SQLException;
import java.util.ArrayList;
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
	 * @param key true if the column is the primary key
	 * @return the created column or null if the creation failed (SQL problem, already exists, incorrect name, ...).
	 */
	JDBCColumn createColumn(String columnName, String type, boolean key);

	/**
	 * Drops a table in the model and the linked database.
	 * @param column column to drop
	 * @return true if the column has been dropped, false otherwise (SQL problem, doesn't exist, ...).
	 */
	boolean dropColumn(JDBCColumn column);

	/**
	 * Grant access for a user
	 * @param access access type
	 * @param user user to grand
	 * @return true if grant is accepted
	 */
	boolean grant(String access, String user);

	JDBCResultSet selectAll();

	JDBCResultSet select(String where);

	JDBCResultSet select(String where, String order, int limit, int offset);

	boolean insert(String[] ... values);

	boolean insert(JDBCLine line);

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
					columns.addAll(SQLHelper.getTableColumns(this, SQLHelper.getFactory(model)));
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Can't read columns on table '"+ getName()+"'", e);
				}
			}
			return columns;
		}

		@Override
		public JDBCColumn getColumn(String name) {
			for (JDBCColumn column : getColumns()) {
				if (column.getName().equalsIgnoreCase(name)) return column;
			}
			return null;
		}

		@Override
		public JDBCColumn createColumn(String columnName, String type, boolean key) {
			try {
				JDBCColumn column = SQLHelper.createColumn(this, SQLHelper.getFactory(getSchema().getModel()), columnName, type, key);
				addColumn(column);
				return column;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't create column '"+ columnName +"' on table '"+ getName()+"'", e);
				return null;
			}
		}

		@Override
		public boolean dropColumn(JDBCColumn column) {
			try {
				SQLHelper.dropColumn(this, column.getName());
				removeColumn(column);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't drop column '"+ column.getName() +"' on table '"+ getName()+"'", e);
				return false;
			}
		}

		@Override
		public boolean grant(String access, String user) {
			JDBCConnection connection = getSchema().getModel();
			try {
				SQLHelper.grant(connection, access, getName(), user);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't grant '" + access +"' on '"+ getName() +"' for user '"+ user +"' in '"+ connection.getAddress() +"'", e);
				return false;
			}
		}

		@Override
		public JDBCResultSet selectAll() {
			return select(null);
		}

		@Override
		public JDBCResultSet select(String where) {
			return select(where, null, -1, 0);
		}

		@Override
		public JDBCResultSet select(String where, String order, int limit, int offset) {
			JDBCConnection model = this.getSchema().getModel();
			try {
				ModelFactory factory = SQLHelper.getFactory(model);
				return SQLHelper.select(model, factory, this, where, order, limit, offset);
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't select from '"+ getName() +"' on '"+ model.getAddress() +"'", e);
				return JDBCResultSet.empty;
			}
		}

		@Override
		public boolean insert(String[] ... values) {
			ModelFactory factory = SQLHelper.getFactory(getSchema().getModel());
			JDBCLine line = factory.newInstance(JDBCLine.class);
			List<JDBCValue> jdbcValues = new ArrayList<>();
			for (String[] value : values) {
				JDBCValue jdbcValue = factory.newInstance(JDBCValue.class);
				jdbcValue.init(line, getColumn(value[0]), value[1]);
				jdbcValues.add(jdbcValue);
			}
			line.init(this, jdbcValues);
			return insert(line);
		}

		@Override
		public boolean insert(JDBCLine line) {
			JDBCConnection model = getSchema().getModel();
			try {
				SQLHelper.insert(line);
				return true;
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't insert into '"+ getName() +"' on '"+ model.getAddress() +"'", e);
				return false;
			}
		}

		@Override
		public String toString() {
			return "[Table] " + getName() + "("+ getColumns().size() +")";
		}
	}
}
