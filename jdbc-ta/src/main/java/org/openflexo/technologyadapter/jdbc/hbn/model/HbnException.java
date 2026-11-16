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

package org.openflexo.technologyadapter.jdbc.hbn.model;

import org.openflexo.foundation.fml.rt.error.FMLTechnologyError;

import jakarta.persistence.PersistenceException;

/**
 * Exception thrown while configuring or executing Hibernate framework: a foreseen failure of the JDBC technology, typed for FML
 * (CORE-F-11 phase 6)
 * 
 * @author sylvain
 *
 */
@SuppressWarnings("serial")
public class HbnException extends FMLTechnologyError {

	/**
	 * Build new exception with supplied message
	 * 
	 * @param message
	 */
	public HbnException(String message) {
		super(message);
	}

	/**
	 * Build new exception with another exception
	 * 
	 * @param exception
	 */
	public HbnException(Exception exception) {
		super(exception.getMessage() != null ? exception.getMessage() : exception.toString(), exception);
	}

	/**
	 * Tell if supplied unchecked exception is a foreseen failure of Hibernate or of the database (a persistence failure, a query that
	 * does not parse, a transaction in the wrong state), to be raised as an {@link HbnException} rather than as a defect of the platform
	 * (CORE-F-11 phase 6): a {@link PersistenceException} (Hibernate's own exceptions included), or an {@link IllegalArgumentException} /
	 * {@link IllegalStateException} thrown by Hibernate or the persistence API itself (JPA reports a query that does not parse and a
	 * transaction in the wrong state that way). The same exceptions thrown by OpenFlexo code stay defects.
	 */
	public static boolean isHibernateFailure(RuntimeException e) {
		if (e instanceof PersistenceException) {
			return true;
		}
		if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
			if (e.getCause() instanceof PersistenceException) {
				return true;
			}
			StackTraceElement[] stack = e.getStackTrace();
			if (stack.length > 0) {
				String thrower = stack[0].getClassName();
				return thrower.startsWith("org.hibernate.") || thrower.startsWith("jakarta.persistence.");
			}
		}
		return false;
	}

}
