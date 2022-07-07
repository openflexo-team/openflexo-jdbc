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
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ReturnException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.hbn.fml.OpenTransaction.OpenTransactionImpl;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnFlexoConceptInstance;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * {@link EditionAction} used to persist an Hibernate object
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(SaveHbnObject.SaveHbnObjectImpl.class)
@XMLElement
@FML("SaveHbnObject")
public interface SaveHbnObject extends TechnologySpecificActionDefiningReceiver<HbnModelSlot, HbnVirtualModelInstance, Void> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String TRANSACTION_KEY = "object";

	@Getter(value = TRANSACTION_KEY)
	@XMLAttribute
	public DataBinding<FlexoConceptInstance> getObject();

	@Setter(TRANSACTION_KEY)
	public void setObject(DataBinding<FlexoConceptInstance> object);

	public static abstract class SaveHbnObjectImpl<T>
			extends TechnologySpecificActionDefiningReceiverImpl<HbnModelSlot, HbnVirtualModelInstance, Void> implements SaveHbnObject {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(OpenTransactionImpl.class.getPackage().getName());

		private DataBinding<FlexoConceptInstance> object;

		@Override
		public DataBinding<FlexoConceptInstance> getObject() {
			if (object == null) {
				object = new DataBinding<>(this, FlexoConceptInstance.class, BindingDefinitionType.GET);
				object.setBindingName(TRANSACTION_KEY);
			}
			return object;
		}

		@Override
		public void setObject(DataBinding<FlexoConceptInstance> anObject) {
			if (anObject != null) {
				anObject.setOwner(this);
				anObject.setBindingName(TRANSACTION_KEY);
				anObject.setDeclaredType(FlexoConceptInstance.class);
				anObject.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.object = anObject;
		}

		@Override
		public Type getAssignableType() {
			return Void.class;
		}

		public HbnFlexoConceptInstance getObject(BindingEvaluationContext evaluationContext) {
			if (getObject().isValid()) {
				try {
					return (HbnFlexoConceptInstance) getObject().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public Void execute(RunTimeEvaluationContext evaluationContext) throws ReturnException, FMLExecutionException {

			System.out.println("Save object " + getObject(evaluationContext));

			HbnVirtualModelInstance vmi = getReceiver(evaluationContext);
			HbnFlexoConceptInstance object = getObject(evaluationContext);

			vmi.getDefaultSession().save(object.getFlexoConcept().getName(), object.getHbnSupportObject());

			return null;
		}
	}
}
