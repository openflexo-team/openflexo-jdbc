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

import java.util.List;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.openflexo.connie.BindingVariable;

public class EntityManagerBindingModel extends AbstractJpaBindingModel<Metamodel> {

	public EntityManagerBindingModel(JpaBindingFactory bindingFactory, Metamodel obj) {
		super(bindingFactory, obj);
	}

	/* TODO: simple for now , needs to ass support for change on notification*/
	@Override
	protected void updateVariables() {

		List<BindingVariable> TO_REMOVE = this.getAccessibleBindingVariables();

		BindingVariable existing = this.getBindingVariableNamed(SELF_PROPERTY_NAME);
		BindingVariable bv;
		if (existing == null) {
			bv = new BindingVariable(SELF_PROPERTY_NAME, EntityManagerCtxt.class);
			addToBindingVariables(bv);
		}
		else {
			TO_REMOVE.remove(existing);
		}

		for (EntityType<?> ent : innerType.getEntities()) {
			existing = this.getBindingVariableNamed(ent.getName());
			if (existing != null) {
				TO_REMOVE.remove(existing);
			}
			else {
				bv = new BindingVariable(ent.getName(), EntityBindingModel.class);
				addToBindingVariables(bv);
			}
		}

		for (BindingVariable abv : TO_REMOVE) {
			this.removeFromBindingVariables(abv);
		}

	}

}
