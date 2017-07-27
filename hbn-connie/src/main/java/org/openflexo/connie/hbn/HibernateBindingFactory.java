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
import java.util.List;

import org.hibernate.type.AbstractType;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.SimplePathElement;

public class HibernateBindingFactory implements BindingFactory {

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {

		if (parent != null) {
			Type pType = parent.getType();

			if (pType instanceof AbstractType) {
				System.out.println("Prout");
			}
		}

		return null;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getTypeForObject(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement arg0, Function arg1, List<DataBinding<?>> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function retrieveFunction(Type arg0, String arg1, List<DataBinding<?>> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
