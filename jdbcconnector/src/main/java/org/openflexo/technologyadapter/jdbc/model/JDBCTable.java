package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper.JoinType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC connector table description
 */
@ModelEntity
@ImplementationClass(JDBCTable.JDBCTableImpl.class)
public interface JDBCTable extends FlexoObject, InnerResourceData<JDBCConnection> {

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

	/**
	 * Finds the line for the given keys
	 * @param keys keys to find on line in the order of the keys in the table
	 * @return the line if it can be found, null if it doesn't exist
	 */
	JDBCLine find(String ... keys);

	/**
	 * Finds the line for the given keys
	 * @param keys keys to find on line in the order of the keys in the table
	 * @return the line if it can be found, null if it doesn't exist
	 */
	JDBCLine find(List<String> keys);

	/**
	 * Selects all lines for the table.
	 * @return a result set contains all line.
	 */
	JDBCResultSet selectAll();

	/**
	 * Selects line in the table that matches the where close.
	 * @param where a SQL where close for the select query
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet select(String where);

	/**
	 * Select line in the table that matches the where close in the given order and limited in size.
	 * @param where a SQL where close for the select query
	 * @param order a SQL order close for the select query
	 * @param limit the limit of result lines
	 * @param offset the offset to start result lines
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet select(String where, String order, int limit, int offset);

	/**
	 * Select all lines with join in the table.
	 * @param joinType type of join.
	 * @param thisOn column for this table to join on
	 * @param otherOn column for another table to join on
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectAllWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn);

	/**
	 * Select lines with join in the table that matches the where close.
	 * @param joinType type of join.
	 * @param thisOn column for this table to join on
	 * @param otherOn column for another table to join on
	 * @param where a SQL where close for the select query
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn, String where);

	/**
	 * Select line with join in the table that matches the where close in the given order and limited in size.
	 * @param joinType type of join.
	 * @param thisOn column for this table to join on
	 * @param otherOn column for another table to join on
	 * @param where a SQL where close for the select query
	 * @param order a SQL order close for the select query
	 * @param limit the limit of result lines
	 * @param offset the offset to start result lines
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn, String where, String order, int limit, int offset);

	/**
	 * Select all line with join in the table.
	 * @param joinType type of join.
	 * @param join table to join with.
	 * @param on a SQL on close for the select query
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectAllWithJoin(JoinType joinType, JDBCTable join, String on);

	/**
	 * Select line with join in the table that matches the where close.
	 * @param joinType type of join.
	 * @param join table to join with.
	 * @param on a SQL on close for the select query
	 * @param where a SQL where close for the select query
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectWithJoin(JoinType joinType, JDBCTable join, String on, String where);

	/**
	 * Select line with join in the table that matches the where close in the given order and limited in size.
	 * @param joinType type of join.
	 * @param join table to join with.
	 * @param on a SQL on close for the select query
	 * @param where a SQL where close for the select query
	 * @param order a SQL order close for the select query
	 * @param limit the limit of result lines
	 * @param offset the offset to start result lines
	 * @return the {@link JDBCResultSet} for this request
	 */
	JDBCResultSet selectWithJoin(JoinType joinType, JDBCTable join, String on, String where, String order, int limit, int offset);

	boolean insert(String[] ... values);

	boolean insert(JDBCLine line);

	abstract class JDBCTableImpl extends FlexoObjectImpl implements JDBCTable {

		private static final Logger LOGGER = Logger.getLogger(JDBCTable.class.getPackage().getName());

		/** Internal counter to avoid too many SQL requests */
		private long lastColumnsUpdate = -1l;

		@Override
		public List<JDBCColumn> getColumns() {
			List<JDBCColumn> columns = (List<JDBCColumn>) performSuperGetter(COLUMNS);
			long currentTimeMillis = System.currentTimeMillis();
			if (lastColumnsUpdate < currentTimeMillis - 200) {
				try {
					lastColumnsUpdate = currentTimeMillis;
					SQLHelper.updateColumns(this, columns, SQLHelper.getFactory(getSchema().getModel()));
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
		public JDBCLine find(String ... keys) {
			return find(Arrays.asList(keys));
		}

		@Override
		public JDBCLine find(List<String> keys) {
			StringBuilder where = new StringBuilder();
			int index = 0;
			for (JDBCColumn column : getColumns()) {
				if (column.isPrimaryKey()) {
					if (where.length() > 0) where.append(" AND ");
					where.append(column.getName());
					where.append("=");
					where.append(SQLHelper.sqlValue(column.getType(), keys.get(index)));
					index += 1;
				}
			}
			JDBCResultSet resultSet = select(where.toString(), null, 1, 0);
			return resultSet.getLines().isEmpty() ? null : resultSet.getLines().get(0);
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
			ModelFactory factory = SQLHelper.getFactory(model);
			try {
				return SQLHelper.select(factory, this, where, order, limit, offset);
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't select from '"+ getName() +"' on '"+ model.getAddress() +"'", e);
				JDBCResultSet result = factory.newInstance(JDBCResultSet.class);
				result.init(this, Collections.<JDBCLine>emptyList());
				return result;
			}
		}

		@Override
		public JDBCResultSet selectAllWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn) {
			return selectWithJoin(joinType, thisOn, otherOn, null, null, -1, -1);
		}

		@Override
		public JDBCResultSet selectWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn, String where) {
			return selectWithJoin(joinType, thisOn, otherOn, where, null, -1, -1);
		}

		@Override
		public JDBCResultSet selectWithJoin(JoinType joinType, JDBCColumn thisOn, JDBCColumn otherOn, String where, String order, int limit, int offset) {
			String on = thisOn.getTable().getName() + "." + thisOn.getName() + " = " + otherOn.getTable().getName() + "." + otherOn.getName();
			return selectWithJoin(joinType, otherOn.getTable(), on, where, order, limit, offset);
		}

		@Override
		public JDBCResultSet selectAllWithJoin(JoinType joinType, JDBCTable join, String on) {
			return selectWithJoin(joinType, join, on, null, null, -1, -1);
		}

		@Override
		public JDBCResultSet selectWithJoin(JoinType joinType, JDBCTable join, String on, String where) {
			return selectWithJoin(joinType, join, on, where, null, -1, -1);
		}

		@Override
		public JDBCResultSet selectWithJoin(JoinType joinType, JDBCTable join, String on, String where, String order, int limit, int offset) {
			JDBCConnection model = this.getSchema().getModel();
			ModelFactory factory = SQLHelper.getFactory(model);
			try {
				return SQLHelper.select(factory, this, joinType, join, on, where, order, limit, offset);
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Can't select from '"+ getName() +"' on '"+ model.getAddress() +"'", e);
				JDBCResultSet result = factory.newInstance(JDBCResultSet.class);
				result.init(this, Collections.<JDBCLine>emptyList());
				return result;
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
		public JDBCConnection getResourceData() {
			return getSchema().getModel();
		}

		@Override
		public String toString() {
			return "[Table] " + getName() + "("+ getColumns().size() +")";
		}
	}
}
