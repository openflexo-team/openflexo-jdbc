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

package org.openflexo.technologyadapter.jdbc.fml.editionaction;

import org.openflexo.connie.DataBinding;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.editionaction.AddJDBCLineValue.AddJDBCLineValueImpl;

@ModelEntity
@ImplementationClass(AddJDBCLineValueImpl.class)
@XMLElement
public interface AddJDBCLineValue {

	@PropertyIdentifier(type = AddJDBCLine.class)
	String OWNER_KEY = "owner";

	@PropertyIdentifier(type = String.class)
	String COLUMN_NAME_KEY = "columnName";

	@PropertyIdentifier(type = DataBinding.class)
	String VALUE_KEY = "value";

	@Getter(OWNER_KEY) @XMLElement
	AddJDBCLine getOwner();

	@Setter(OWNER_KEY)
	void setOwner(AddJDBCLine owner);

	@Getter(COLUMN_NAME_KEY)
	@XMLAttribute
	String getColumnName();

	@Setter(COLUMN_NAME_KEY)
	void setColumnName(String paramName);

	@Getter(value = VALUE_KEY)  @XMLAttribute
	DataBinding<String> getValue();

	@Setter(VALUE_KEY)
	void setValue(DataBinding<String> value);

	abstract class AddJDBCLineValueImpl implements AddJDBCLineValue {

		private DataBinding<String> value;

		// Use it only for deserialization
		public AddJDBCLineValueImpl() {
			super();
		}

		@Override
		public DataBinding<String> getValue() {
			if (value == null) {
				value = new DataBinding<>(getOwner(), String.class, DataBinding.BindingDefinitionType.GET);
				String columnName = getColumnName();
				value.setBindingName(columnName != null ? columnName : "column1");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<String> value) {
			if (value != null) {
				value.setOwner(getOwner());
				String columnName = getColumnName();
				value.setBindingName(columnName != null ? columnName : "column1");
				value.setDeclaredType(String.class);
				value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.value = value;
		}

	}

}
