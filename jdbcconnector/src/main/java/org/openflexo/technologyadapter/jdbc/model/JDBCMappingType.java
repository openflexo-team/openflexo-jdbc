/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.jdbc.model;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;

/**
 * Hibernate mappings types: each driver should handle it
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public enum JDBCMappingType {

	STRING {
		@Override
		public Type getJavaType() {
			return String.class;
		}

		@Override
		public Object encodeObjectForStoring(Object objectToBeStored) {
			if (objectToBeStored instanceof String) {
				return objectToBeStored;
			}
			if (objectToBeStored != null) {
				logger.warning("Inconsistent data " + objectToBeStored + " as JDBC STRING");
			}
			return null;
		}
	},
	INTEGER {
		@Override
		public Type getJavaType() {
			return Integer.class;
		}

		@Override
		public Object encodeObjectForStoring(Object objectToBeStored) {
			if (objectToBeStored instanceof Integer) {
				return objectToBeStored;
			}
			if (objectToBeStored instanceof Long || objectToBeStored instanceof Short || objectToBeStored instanceof Byte) {
				return TypeUtils.castTo(objectToBeStored, Integer.class);
			}
			if (objectToBeStored != null) {
				logger.warning("Inconsistent data " + objectToBeStored + " as JDBC INTEGER");
			}
			return null;
		}
	},
	FLOAT {
		@Override
		public Type getJavaType() {
			return Float.class;
		}

		@Override
		public Object encodeObjectForStoring(Object objectToBeStored) {
			if (objectToBeStored instanceof Float) {
				return objectToBeStored;
			}
			if (objectToBeStored instanceof Number) {
				return TypeUtils.castTo(objectToBeStored, Float.class);
			}
			if (objectToBeStored != null) {
				logger.warning("Inconsistent data " + objectToBeStored + " as JDBC FLOAT");
			}
			return null;
		}
	},
	DATE {
		@Override
		public Type getJavaType() {
			return Date.class;
		}

		@Override
		public Object encodeObjectForStoring(Object objectToBeStored) {
			if (objectToBeStored instanceof Date) {
				return objectToBeStored;
			}
			if (objectToBeStored != null) {
				logger.warning("Inconsistent data " + objectToBeStored + " as JDBC DATE");
			}
			return null;
		}
	};

	public abstract Type getJavaType();

	public abstract Object encodeObjectForStoring(Object objectToBeStored);

	public static JDBCMappingType getJDBCMappingType(String typeAsString) {
		if (typeAsString.equalsIgnoreCase("INTEGER")) {
			return JDBCMappingType.INTEGER;
		}
		else if (typeAsString.equalsIgnoreCase("FLOAT")) {
			return JDBCMappingType.FLOAT;
		}
		else if (typeAsString.equalsIgnoreCase("VARCHAR")) {
			return JDBCMappingType.STRING;
		}
		else if (typeAsString.toUpperCase().contains("CHAR")) {
			return JDBCMappingType.STRING;
		}
		else if (typeAsString.equalsIgnoreCase("DATE")) {
			return JDBCMappingType.DATE;
		}
		return JDBCMappingType.STRING;

	}

	private static final Logger logger = Logger.getLogger(JDBCMappingType.class.getPackage().getName());

}
