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

package org.openflexo.technologyadapter.jdbc.fml;

import java.util.ArrayList;
import java.util.List;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCLineActorReference.JDBCLineActorReferenceImpl.class)
public interface JDBCLineActorReference extends JDBCActorReference<JDBCLine> {

	String RESULTSET_DESCRIPTION = "resultsetDescription";

	String KEYS = "keys";

	@Getter(RESULTSET_DESCRIPTION) @XMLElement(xmlTag = "resultSet")

	JDBCResultSetDescription getResultSetDescription();

	@Setter(RESULTSET_DESCRIPTION)
	void setResultSetDescription(JDBCResultSetDescription resultRestDescription);

	@Getter(value=KEYS, cardinality = Getter.Cardinality.LIST) @XMLElement(xmlTag = "keys")
	List<String> getKeys();

	@Adder(KEYS)
	void addToKeys(String key);

	@Remover(KEYS)
	void removeFromKeys(String key);

	abstract class JDBCLineActorReferenceImpl extends JDBCActorReferenceImpl<JDBCLine> implements JDBCLineActorReference {

		private JDBCLine line;

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			if (line != null) return line.getResultSet().getResultSetDescription();
			return (JDBCResultSetDescription) performSuperGetter(RESULTSET_DESCRIPTION);
		}

		@Override
		public List<String> getKeys() {
			if (line != null) {
				List<String> keys = new ArrayList<>();
				for (JDBCValue value : line.getValues()) {
					if (value.getColumn().isPrimaryKey()) {
						keys.add(value.getValue());
					}
				}
				return keys;
			}
			return (List<String>) performSuperGetter(KEYS);
		}

		@Override
		public JDBCLine getModellingElement() {
			if (line == null) {
				JDBCConnection connection = getConnection();
				if (connection != null) {
					JDBCResultSet resultSet = connection.select(getResultSetDescription());
					setModellingElement(resultSet.find(getKeys()));
				}
			}
			return line;
		}

		@Override
		public void setModellingElement(JDBCLine object) {
			if (line != object) {
				Object oldValue = line;
				line = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
