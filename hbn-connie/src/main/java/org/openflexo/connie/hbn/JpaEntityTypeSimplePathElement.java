/**
 * 
 * Copyright (c) 2017, Openflexo
 * 
 * This file is part of Csvconnector, a component of the software infrastructure 
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

package org.openflexo.connie.hbn;

import java.lang.reflect.Type;

import javax.persistence.metamodel.EntityType;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;

public class JpaEntityTypeSimplePathElement extends SimplePathElement {

	private EntityType<?> entityType = null;

	public JpaEntityTypeSimplePathElement(BindingPathElement parent, EntityType<?> entType, Type type) {
		super(parent, entType.getName(), type);
		entityType = entType;
	}

	@Override
	public Object getBindingValue(Object arg0, BindingEvaluationContext arg1)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {
		if (arg0 instanceof EntityManagerCtxt) {
			return entityType;
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public String getLabel() {
		return entityType.getName();
	}

	@Override
	public String getTooltipText(Type arg0) {
		if (entityType != null) {
			return "Jpa Entity<" + entityType.getName() + ">";
		}
		else {
			return "Jpa Entity<unknown>";
		}
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		return;
	}

	public EntityType<?> getEntityType() {
		return entityType;
	}
}