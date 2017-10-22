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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.editionaction.TechnologySpecificAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext.ReturnException;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.hbn.fml.OpenTransaction.OpenTransactionImpl;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * {@link EditionAction} used to rollback an Hibernate {@link Transaction}
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(RollbackTransaction.RollbackTransactionImpl.class)
@XMLElement
@FML("OpenTransaction")
public interface RollbackTransaction extends TechnologySpecificAction<HbnModelSlot, HbnVirtualModelInstance, Void> {

	/*@PropertyIdentifier(type = DataBinding.class)
	public static final String TRANSACTION_KEY = "transaction";
	
	@Getter(value = TRANSACTION_KEY)
	@XMLAttribute
	public DataBinding<Transaction> getTransaction();
	
	@Setter(TRANSACTION_KEY)
	public void setTransaction(DataBinding<Transaction> transaction);*/

	public static abstract class RollbackTransactionImpl<T>
			extends TechnologySpecificActionImpl<HbnModelSlot, HbnVirtualModelInstance, Void> implements RollbackTransaction {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(OpenTransactionImpl.class.getPackage().getName());

		/*private DataBinding<Transaction> transaction;
		
		@Override
		public DataBinding<Transaction> getTransaction() {
			if (transaction == null) {
				transaction = new DataBinding<Transaction>(this, Transaction.class, BindingDefinitionType.GET);
				transaction.setBindingName(TRANSACTION_KEY);
			}
			return transaction;
		}
		
		@Override
		public void setTransaction(DataBinding<Transaction> aTransaction) {
			if (aTransaction != null) {
				aTransaction.setOwner(this);
				aTransaction.setBindingName(TRANSACTION_KEY);
				aTransaction.setDeclaredType(Transaction.class);
				aTransaction.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.transaction = aTransaction;
		}*/

		@Override
		public Type getAssignableType() {
			return Void.class;
		}

		/*public Transaction getTransaction(BindingEvaluationContext evaluationContext) {
			if (getTransaction().isValid()) {
				try {
					return getTransaction().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}*/

		@Override
		public Void execute(RunTimeEvaluationContext evaluationContext) throws ReturnException, FlexoException {

			System.out.println("Rollback transaction for " + getReceiver(evaluationContext));

			HbnVirtualModelInstance vmi = getReceiver(evaluationContext);

			vmi.rollback();

			return null;
		}
	}
}
