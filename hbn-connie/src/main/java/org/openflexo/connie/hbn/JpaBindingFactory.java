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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.type.AbstractType;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.java.JavaBindingFactory;

public class JpaBindingFactory extends JavaBindingFactory {

	static final Logger logger = Logger.getLogger(JpaBindingFactory.class.getPackage().getName());

	// Hibernate mapping Model
	private Metamodel metamodel;

	public JpaBindingFactory(Metamodel model) {
		super();
		metamodel = model;
	}

	/**
	 * 
	 * @param object
	 * @param parent
	 * @return
	 */
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		if (object instanceof EntityType) {

			return new JpaEntitySimplePathElement(parent, (EntityType<?>) object, EntityType.class);
		}
		if (object instanceof Attribute) {
			return new JpaAttributeSimplePathElement(parent, (Attribute<?, ?>) object);
		}

		logger.warning("Unexpected " + object + " for parent=" + parent);
		return null;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {

		if (parent != null) {
			Type pType = parent.getType();

			if (pType instanceof AbstractType) {
				System.out.println("Prout");
			}
		}

		return super.getAccessibleFunctionPathElements(parent);
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		if (parent != null) {
			Type pType = parent.getType();
			if (pType instanceof Attribute) {
				List<SimplePathElement> returned = new ArrayList<SimplePathElement>();

				Collections.sort(returned, BindingPathElement.COMPARATOR);
				return returned;
			}
			else if (pType instanceof EntityType) {
				List<SimplePathElement> returned = new ArrayList<SimplePathElement>();

				Collections.sort(returned, BindingPathElement.COMPARATOR);
				return returned;
			}
			else if (pType instanceof Metamodel) {
				List<SimplePathElement> returned = new ArrayList<SimplePathElement>();

				Collections.sort(returned, BindingPathElement.COMPARATOR);
				return returned;
			}
			else
				return super.getAccessibleSimplePathElements(parent);
		}
		else {
			logger.warning("Trying to find accessible path elements for a NULL parent");
			return Collections.emptyList();
		}
	}

	@Override
	public Type getTypeForObject(Object arg0) {
		return super.getTypeForObject(arg0);
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement arg0, Function arg1, List<DataBinding<?>> arg2) {
		return super.makeFunctionPathElement(arg0, arg1, arg2);
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement arg0, String arg1) {
		if (arg0 != null) {
			Type objectType = arg0.getType();
			if (objectType == EntityManagerCtxt.class) {
				for (EntityType<?> f : this.getMetamodel().getEntities()) {
					if (f.getName().equals(arg1)) {
						return makeSimplePathElement(f, arg0);
					}
				}
			}
			else if (arg0 instanceof JpaEntitySimplePathElement) {
				EntityType<?> entityType = ((JpaEntitySimplePathElement) arg0).getEntityType();
				Attribute<?, ?> attr = entityType.getAttribute(arg1);
				if (attr != null) {
					return makeSimplePathElement(attr, arg0);
				}
			}

		}
		return super.makeSimplePathElement(arg0, arg1);

	}

	@Override
	public Function retrieveFunction(Type arg0, String arg1, List<DataBinding<?>> arg2) {
		return super.retrieveFunction(arg0, arg1, arg2);
	}

	public Metamodel getMetamodel() {
		return metamodel;
	}

}
