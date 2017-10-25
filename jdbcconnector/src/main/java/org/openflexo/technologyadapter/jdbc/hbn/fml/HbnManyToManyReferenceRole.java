/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
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

package org.openflexo.technologyadapter.jdbc.hbn.fml;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.PropertyCardinality;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

/**
 * A role specific to Hibernate technology (HbnModelSlot) allowing to access a collection of referenced objects in an external table through
 * another table and two foreign keys
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(HbnManyToManyReferenceRole.HbnToManyReferenceRoleImpl.class)
@XMLElement
public interface HbnManyToManyReferenceRole extends FlexoConceptInstanceRole {

	@PropertyIdentifier(type = String.class)
	String RELATION_TABLE_NAME_KEY = "relationTableName";
	@PropertyIdentifier(type = String.class)
	String SOURCE_KEY_ATTRIBUTE_NAME_KEY = "sourceKeyAttributeName";
	@PropertyIdentifier(type = String.class)
	String DESTINATION_KEY_ATTRIBUTE_NAME_KEY = "destinationKeyAttributeName";

	@Getter(RELATION_TABLE_NAME_KEY)
	@XMLAttribute
	String getRelationTableName();

	@Setter(RELATION_TABLE_NAME_KEY)
	void setRelationTableName(String relationTableName);

	@Getter(SOURCE_KEY_ATTRIBUTE_NAME_KEY)
	@XMLAttribute
	String getSourceKeyAttributeName();

	@Setter(SOURCE_KEY_ATTRIBUTE_NAME_KEY)
	void setSourceKeyAttributeName(String sourceKeyAttributeName);

	@Getter(DESTINATION_KEY_ATTRIBUTE_NAME_KEY)
	@XMLAttribute
	String getDestinationKeyAttributeName();

	@Setter(DESTINATION_KEY_ATTRIBUTE_NAME_KEY)
	void setDestinationKeyAttributeName(String destinationKeyAttributeName);

	public static abstract class HbnToManyReferenceRoleImpl extends FlexoConceptInstanceRoleImpl implements HbnManyToManyReferenceRole {

		private static final Logger logger = Logger.getLogger(HbnToManyReferenceRoleImpl.class.getPackage().getName());

		@Override
		public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		@Override
		public PropertyCardinality getCardinality() {
			switch (super.getCardinality()) {
				case ZeroOne:
					return PropertyCardinality.ZeroMany;
				case One:
					return PropertyCardinality.OneMany;
				case ZeroMany:
					return PropertyCardinality.ZeroMany;
				case OneMany:
					return PropertyCardinality.OneMany;
				default:
					break;
			}
			return PropertyCardinality.ZeroMany;
		}
	}
}
