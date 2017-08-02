/**
 * 
 * Copyright (c) 2017, Openflexo
 * This file is part of Connie-core, a component of the software infrastructure 
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

import java.beans.PropertyChangeSupport;

import javax.persistence.EntityManager;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;

public class EntityManagerCtxt implements Bindable, BindingEvaluationContext {

	private EntityManager em = null;
	private EntityManagerBindingModel bindingModel = null;
	private JpaBindingFactory factory = null;

	static protected String SELF_PROPERTY_NAME = "self";

	public EntityManagerCtxt(JpaBindingFactory bindingFactory, EntityManager anEm) {
		super();
		em = anEm;
		factory = bindingFactory;
		if (em != null & factory != null)
			bindingModel = new EntityManagerBindingModel(factory, em.getMetamodel());
	}

	@Override
	public BindingFactory getBindingFactory() {
		return factory;
	}

	@Override
	public Object getValue(BindingVariable variable) {

		if (variable.getVariableName().equals(SELF_PROPERTY_NAME)) {
			return this;
		}

		System.out.println(" Moi Je chercher la valeur de " + variable.getLabel());
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		if (bindingModel == null & em != null & factory != null)
			bindingModel = new EntityManagerBindingModel(factory, em.getMetamodel());
		return bindingModel;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

}
