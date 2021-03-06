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

import org.openflexo.foundation.fml.AbstractActionScheme;
import org.openflexo.foundation.fml.TechnologySpecificFlexoBehaviour;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * Behaviour beeing called when initializing a {@link HbnVirtualModelInstance} (which is created or reloaded)<br>
 * 
 * This is a hook to perform required computation on a {@link HbnVirtualModelInstance} at reload.<br>
 * Note that related {@link VirtualModel} should not define more than one {@link HbnInitializer}
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(HbnInitializer.HbnInitializerImpl.class)
@XMLElement
@FML("HbnInitializer")
public interface HbnInitializer extends AbstractActionScheme, TechnologySpecificFlexoBehaviour<JDBCTechnologyAdapter> {

	public static abstract class HbnInitializerImpl extends AbstractActionSchemeImpl implements HbnInitializer {

		@Override
		public JDBCTechnologyAdapter getSpecificTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return null;
		}

	}

	@DefineValidationRule
	public static class OnlyOneHbnInitializer extends ValidationRule<OnlyOneHbnInitializer, HbnInitializer> {
		public OnlyOneHbnInitializer() {
			super(HbnInitializer.class, "only_one_initializer_should_be_defined_for_one_virtual_model");
		}

		@Override
		public ValidationIssue<OnlyOneHbnInitializer, HbnInitializer> applyValidation(HbnInitializer initializer) {

			if (initializer.getFlexoConcept() instanceof VirtualModel
					&& ((VirtualModel) initializer.getFlexoConcept()).getFlexoBehaviours(HbnInitializer.class).size() > 1) {
				return new ValidationError<>(this, initializer, "more_than_one_initializer_defined");
			}
			return null;
		}

	}

}
