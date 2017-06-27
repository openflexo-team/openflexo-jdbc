/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.technologyadapter.jdbc.model;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Remover;
import org.openflexo.technologyadapter.jdbc.util.SQLHelper;

/**
 * JDBC Schema. It contains table description for the connected JDBC resource.
 */
@ModelEntity
@ImplementationClass(JDBCSchema.JDBCSchemaImpl.class)
public interface JDBCSchema extends FlexoObject, InnerResourceData<JDBCConnection> {

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

			// TODO adds to preferences
			int tempo = 1000 * 60 * 60;

			if (lastTableUpdate < currentTimeMillis - tempo) {
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
		public JDBCConnection getResourceData() {
			return getModel();
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
