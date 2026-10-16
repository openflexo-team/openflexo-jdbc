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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * JDBC result set
 */
@ModelEntity
@ImplementationClass(JDBCResultSet.JDBCResultSetImpl.class)
public interface JDBCResultSet extends FlexoObject, InnerResourceData<JDBCConnection> {

	// TODO add **readonly** attribute

	void init(JDBCConnection connection, JDBCResultSetDescription description, List<JDBCLine> lines);

	JDBCConnection getConnection();

	/*@Getter("resultSetDescription")*/ @Embedded
	JDBCResultSetDescription getResultSetDescription();

	List<JDBCLine> getLines();

	JDBCLine find(String... keys);

	JDBCLine find(List<String> keys);

	abstract class JDBCResultSetImpl extends FlexoObjectImpl implements JDBCResultSet {

		private JDBCConnection connection;
		private JDBCResultSetDescription description;
		private Map<String, JDBCLine> lines = new LinkedHashMap<>();

		@Override
		public void init(JDBCConnection connection, JDBCResultSetDescription description, List<JDBCLine> lines) {
			this.connection = connection;
			this.description = description;
			for (JDBCLine line : lines) {
				this.lines.put(constructHash(line), line);
			}
		}

		private static String constructHash(JDBCLine line) {
			StringBuilder hash = new StringBuilder();
			for (JDBCValue value : line.getValues()) {
				if (value.getColumn().isPrimaryKey()) {
					if (hash.length() > 0)
						hash.append("-");
					hash.append(value.getValue());
				}
			}
			return hash.toString();
		}

		@Override
		public JDBCConnection getConnection() {
			return connection;
		}

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			return description;
		}

		@Override
		public List<JDBCLine> getLines() {
			return new ArrayList<>(lines.values());
		}

		@Override
		public JDBCLine find(String... keys) {
			return find(Arrays.asList(keys));
		}

		@Override
		public JDBCLine find(List<String> keys) {
			StringBuilder hash = new StringBuilder();
			for (String key : keys) {
				if (hash.length() > 0)
					hash.append("-");
				hash.append(key);
			}
			return lines.get(hash.toString());
		}

		@Override
		public JDBCConnection getResourceData() {
			return getConnection();
		}
	}

}
