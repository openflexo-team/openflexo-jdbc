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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.PamelaResourceModelFactory;
import org.openflexo.foundation.action.FlexoUndoManager;
import org.openflexo.foundation.resource.PamelaResourceImpl.IgnoreLoadingEdits;
import org.openflexo.model.converter.RelativePathResourceConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContext;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

/**
 * one JDBCFactory
 *
 * @author charlie
 *
 */
public class JDBCFactory extends ModelFactory implements PamelaResourceModelFactory<JDBCResource> {

	private static final Logger logger = Logger.getLogger(JDBCFactory.class.getPackage().getName());

	private final JDBCResource resource;

	private FlexoUndoManager undoManager = null;
	private IgnoreLoadingEdits ignoreHandler = null;

	public JDBCFactory() throws ModelDefinitionException {
		this(null, null);
	}

	public JDBCFactory(JDBCResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(JDBCConnection.class);
		this.resource = resource;
		setEditingContext(editingContext);
		addConverter(new RelativePathResourceConverter(null));
	}

	@Override
	public JDBCResource getResource() {
		return resource;
	}

	/**
	 * Creates empty model that needs to be initialized
	 * 
	 * @return the created model
	 */
	public JDBCConnection makeEmptyModel() {
		return newInstance(JDBCConnection.class);
	}

	public JDBCConnection makeNewModel(JDBCDbType dbType, String address, String user, String password) {
		JDBCConnection returned = newInstance(JDBCConnection.class);
		returned.setAddress(address);
		returned.setUser(user);
		returned.setPassword(password);
		returned.setDbType(dbType);

		// System.out.println("Connect to " + address + " user=" + user + " password=" + password);
		if (returned.getConnection() != null) {
			logger.info("Successfully opening connection on " + address);
		}
		else if (returned.getException() != null) {
			logger.warning("Cannot open JDBC connection: " + returned.getException().getMessage());
		}
		else {
			logger.warning("Cannot open JDBC connection");
		}
		return returned;
	}

	public JDBCResultSet emptyResultSet(JDBCConnection connection) {
		JDBCResultSet result = newInstance(JDBCResultSet.class);
		result.init(connection, null, Collections.<JDBCLine> emptyList());
		return result;
	}

	private JDBCColumn findColumn(int index, ResultSetMetaData metaData, JDBCSchema schema) throws SQLException {
		String tableName = metaData.getTableName(index);
		String columnName = metaData.getColumnName(index);
		return schema.getTable(tableName).getColumn(columnName);
	}

	/** Create JDBCResultSet from a ResultSet */
	public JDBCResultSet makeJDBCResult(JDBCResultSetDescription description, ResultSet resultSet, JDBCSchema schema) throws SQLException {
		JDBCResultSet result = newInstance(JDBCResultSet.class);

		ResultSetMetaData metaData = resultSet.getMetaData();
		// searches for columns
		int columnCount = metaData.getColumnCount();
		JDBCColumn[] columns = new JDBCColumn[columnCount];
		for (int i = 1; i <= columnCount; i++) {
			columns[i - 1] = findColumn(i, metaData, schema);
		}

		List<JDBCLine> lines = new ArrayList<>();
		while (resultSet.next()) {
			JDBCLine line = newInstance(JDBCLine.class);
			List<JDBCValue> values = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				JDBCValue value = newInstance(JDBCValue.class);
				value.init(line, columns[i - 1], resultSet.getString(i));
				values.add(value);
			}
			line.init(result, values);
			lines.add(line);

		}

		result.init(schema.getModel(), description, lines);
		return result;
	}

	public JDBCResultSetDescription makeResultSetDescription(JDBCConnection connection, String from, String joinType, String join,
			String on, String where, String orderBy, int limit, int offset) {
		JDBCResultSetDescription description = newInstance(JDBCResultSetDescription.class);
		description.init(connection, from, joinType, join, on, where, orderBy, limit, offset);
		return description;
	}

	@Override
	public void startDeserializing() {
		startIgnoringEdits();
	}

	@Override
	public void stopDeserializing() {
		stopIgnoringEdits();
	}

	public void startIgnoringEdits() {
		EditingContext editingContext = getResource().getServiceManager().getEditingContext();

		if (editingContext != null && editingContext.getUndoManager() instanceof FlexoUndoManager) {
			undoManager = (FlexoUndoManager) editingContext.getUndoManager();
			ignoreHandler = new IgnoreLoadingEdits(resource);

			undoManager.addToIgnoreHandlers(ignoreHandler);
		}
	}

	public void stopIgnoringEdits() {
		if (ignoreHandler != null) {
			undoManager.removeFromIgnoreHandlers(ignoreHandler);
			ignoreHandler = null;
		}
	}

}
