/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
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

import java.lang.reflect.Type;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.PropertyCardinality;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCMappingType;

@ModelEntity
@ImplementationClass(HbnColumnRole.HbnColumnRoleImpl.class)
@XMLElement
@FML("HbnColumnRole")
public interface HbnColumnRole<T> extends FlexoRole<T> {

	@PropertyIdentifier(type = String.class)
	String COLUMN_NAME_KEY = "columnName";
	@PropertyIdentifier(type = String.class)
	String DATA_TYPE_KEY = "dataType";

	@Getter(COLUMN_NAME_KEY)
	@XMLAttribute
	public String getColumnName();

	@Setter(COLUMN_NAME_KEY)
	public void setColumnName(String columnName);

	@Getter(DATA_TYPE_KEY)
	@XMLAttribute
	public JDBCMappingType getDataType();

	@Setter(DATA_TYPE_KEY)
	public void setDataType(JDBCMappingType dataType);

	public abstract static class HbnColumnRoleImpl<T> extends FlexoRoleImpl<T> implements HbnColumnRole<T> {

		@Override
		public PropertyCardinality getCardinality() {
			return PropertyCardinality.One;
		}

		@Override
		public Type getType() {
			if (getDataType() != null) {
				return getDataType().getJavaType();
			}
			return Object.class;
		}

		@Override
		public void setDataType(JDBCMappingType dataType) {
			performSuperSetter(DATA_TYPE_KEY, dataType);
			notifyResultingTypeChanged();
		}

		/**
		 * Encodes the default cloning strategy
		 * 
		 * @return
		 */
		@Override
		public RoleCloningStrategy defaultCloningStrategy() {
			return RoleCloningStrategy.Reference;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return false;
		}

		@Override
		public ActorReference<T> makeActorReference(T object, FlexoConceptInstance fci) {
			return null;
		}

		@Override
		public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

	}

}
