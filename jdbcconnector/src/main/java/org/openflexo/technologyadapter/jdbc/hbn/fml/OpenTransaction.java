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

import org.hibernate.Transaction;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificActionDefiningReceiver;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
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
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * {@link EditionAction} used to instantiate a new Hibernate {@link Transaction}<br>
 * 
 * When existing and active, current {@link Transaction} will be commited prior to begin transaction
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(OpenTransaction.OpenTransactionImpl.class)
@XMLElement
@FML("OpenTransaction")
public interface OpenTransaction extends TechnologySpecificActionDefiningReceiver<HbnModelSlot, HbnVirtualModelInstance, Transaction> {

	@PropertyIdentifier(type = Integer.class)
	public static final String TIME_OUT_KEY = "timeOut";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ROLL_BACK_ONLY_KEY = "rollbackOnly";

	/**
	 * Time-out in seconds
	 * 
	 * @return
	 */
	@Getter(value = TIME_OUT_KEY)
	@XMLAttribute
	public Integer getTimeOut();

	@Setter(TIME_OUT_KEY)
	public void setTimeOut(Integer timeOut);

	@Getter(value = ROLL_BACK_ONLY_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getRollbackOnly();

	@Setter(ROLL_BACK_ONLY_KEY)
	public void setRollbackOnly(boolean rollbackOnly);

	public boolean hasTimeOut();

	public void setHasTimeOut(boolean hasTimeOut);

	public static abstract class OpenTransactionImpl<T> extends
			TechnologySpecificActionDefiningReceiverImpl<HbnModelSlot, HbnVirtualModelInstance, Transaction> implements OpenTransaction {

		private static final Logger logger = Logger.getLogger(OpenTransactionImpl.class.getPackage().getName());

		@Override
		public Type getAssignableType() {
			return Transaction.class;
		}

		@Override
		public boolean hasTimeOut() {
			return getTimeOut() != null;
		}

		@Override
		public void setHasTimeOut(boolean hasTimeOut) {
			if (hasTimeOut) {
				if (getTimeOut() == null) {
					setTimeOut(60);
					getPropertyChangeSupport().firePropertyChange("hasTimeOut", false, true);
				}
			}
			else {
				if (getTimeOut() != null) {
					setTimeOut(null);
					getPropertyChangeSupport().firePropertyChange("hasTimeOut", true, false);
				}
			}
		}

		@Override
		public Transaction execute(RunTimeEvaluationContext evaluationContext) throws ReturnException, FMLExecutionException {

			System.out.println("OpenTransaction for " + getReceiver(evaluationContext));

			HbnVirtualModelInstance vmi = getReceiver(evaluationContext);

			System.out.println("receiver: " + getReceiver() + " = " + vmi);
			System.out.println("valid: " + getReceiver().isValid() + " reason: " + getReceiver().invalidBindingReason());

			if (vmi == null) {
				logger.warning("Could not OpenTransaction on null HbnVirtualModelInstance");
				return null;
			}

			Transaction returned = vmi.beginTransaction();

			if (getRollbackOnly()) {
				returned.setRollbackOnly();
			}

			if (getTimeOut() != null) {
				returned.setTimeout(getTimeOut());
			}

			return returned;
		}
	}
}
