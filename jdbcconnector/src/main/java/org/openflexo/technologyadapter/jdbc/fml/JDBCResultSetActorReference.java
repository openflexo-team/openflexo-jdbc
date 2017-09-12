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

import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCResultSetActorReference.JDBCResultSetActorReferenceImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription;

/**
 * Actor reference for a ResultSet
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCResultSetActorReferenceImpl.class)
public interface JDBCResultSetActorReference extends JDBCActorReference<JDBCResultSet> {

	String RESULTSET_DESCRIPTION = "requestDescription";

	@Getter(RESULTSET_DESCRIPTION)
	@Embedded
	JDBCResultSetDescription getResultSetDescription();

	@Setter(RESULTSET_DESCRIPTION)
	void setResultSetDescription(JDBCResultSetDescription newDescription);

	abstract class JDBCResultSetActorReferenceImpl extends JDBCActorReferenceImpl<JDBCResultSet> implements JDBCResultSetActorReference {

		private JDBCResultSet resultSet;

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			if (resultSet != null)
				return resultSet.getResultSetDescription();
			return (JDBCResultSetDescription) performSuperGetter(RESULTSET_DESCRIPTION);
		}

		@Override
		public JDBCResultSet getModellingElement(boolean forceLoading) {
			if (resultSet == null) {
				resultSet = getConnection().select(getResultSetDescription());
			}
			return resultSet;
		}

		@Override
		public void setModellingElement(JDBCResultSet object) {
			if (resultSet != object) {
				Object oldValue = resultSet;
				resultSet = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
