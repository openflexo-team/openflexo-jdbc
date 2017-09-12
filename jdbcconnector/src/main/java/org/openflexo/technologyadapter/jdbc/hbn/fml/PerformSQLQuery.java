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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.query.Query;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.FMLRepresentationContext;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.FetchRequest;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnException;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnFlexoConceptInstance;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * A {@link FetchRequest} used to perform a JDBC SQL-query
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(PerformSQLQuery.PerformSQLQueryImpl.class)
@XMLElement
@FML("PerformSQLQuery")
public interface PerformSQLQuery extends FetchRequest<HbnModelSlot, HbnVirtualModelInstance, HbnFlexoConceptInstance> {

	@PropertyIdentifier(type = String.class)
	public static final String FLEXO_CONCEPT_TYPE_URI_KEY = "flexoConceptTypeURI";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONTAINER_KEY = "container";

	@Getter(value = CONTAINER_KEY)
	@XMLAttribute
	public DataBinding<FlexoConceptInstance> getContainer();

	@Setter(CONTAINER_KEY)
	public void setContainer(DataBinding<FlexoConceptInstance> container);

	@Getter(value = FLEXO_CONCEPT_TYPE_URI_KEY)
	@XMLAttribute
	public String _getFlexoConceptTypeURI();

	@Setter(FLEXO_CONCEPT_TYPE_URI_KEY)
	public void _setFlexoConceptTypeURI(String flexoConceptTypeURI);

	public FlexoConcept getFlexoConceptType();

	public void setFlexoConceptType(FlexoConcept flexoConceptType);

	/**
	 * Return the {@link VirtualModel} beeing addressed by this action, according to the {@link #getVirtualModelInstance()} binding
	 * 
	 * @return
	 */
	public VirtualModel getAddressedVirtualModel();

	public static abstract class PerformSQLQueryImpl
			extends FetchRequestImpl<HbnModelSlot, HbnVirtualModelInstance, HbnFlexoConceptInstance> implements PerformSQLQuery {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(PerformSQLQueryImpl.class.getPackage().getName());

		private FlexoConcept flexoConceptType;
		private String flexoConceptTypeURI;

		@Override
		public JDBCTechnologyAdapter getModelSlotTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return (JDBCTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		private DataBinding<FlexoConceptInstance> container;

		@Override
		public DataBinding<FlexoConceptInstance> getContainer() {
			if (container == null) {
				container = new DataBinding<FlexoConceptInstance>(this, FlexoConceptInstance.class, BindingDefinitionType.GET);
				container.setBindingName("container");
			}
			return container;
		}

		@Override
		public void setContainer(DataBinding<FlexoConceptInstance> container) {
			if (container != null) {
				container.setOwner(this);
				container.setBindingName("container");
				container.setDeclaredType(FlexoConceptInstance.class);
				container.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.container = container;
		}

		@Override
		public String getParametersStringRepresentation() {
			String whereClauses = getWhereClausesFMLRepresentation(null);
			return "(type=" + (getFlexoConceptType() != null ? getFlexoConceptType().getName() : "null")
					+ (whereClauses != null ? "," + whereClauses : "") + ")";
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return getReceiver().toString() + "." + getImplementedInterface().getSimpleName() + "("
					+ (getFlexoConceptType() != null ? "type=" + getFlexoConceptType().getName() : "type=?")
					+ (getConditions().size() > 0 ? ",where=" + getWhereClausesFMLRepresentation(context) : "") + ")";
		}

		@Override
		public FlexoConceptInstanceType getFetchedType() {
			return FlexoConceptInstanceType.getFlexoConceptInstanceType(getFlexoConceptType());
		}

		@Override
		public String _getFlexoConceptTypeURI() {
			if (flexoConceptType != null) {
				return flexoConceptType.getURI();
			}
			return flexoConceptTypeURI;
		}

		@Override
		public void _setFlexoConceptTypeURI(String flexoConceptURI) {
			this.flexoConceptTypeURI = flexoConceptURI;
		}

		private boolean isComputingFlexoConceptType = false;

		@Override
		public FlexoConcept getFlexoConceptType() {

			if (!isComputingFlexoConceptType && flexoConceptType == null && flexoConceptTypeURI != null) {
				isComputingFlexoConceptType = true;
				if (getAddressedVirtualModel() != null) {
					flexoConceptType = getAddressedVirtualModel().getFlexoConcept(flexoConceptTypeURI);
				}
				isComputingFlexoConceptType = false;
			}

			return flexoConceptType;
		}

		private boolean isAnalyzingContainer = false;

		/**
		 * Return the {@link VirtualModel} beeing addressed by this action, according to the {@link #getVirtualModelInstance()} binding
		 * 
		 * @return
		 */
		@Override
		public VirtualModel getAddressedVirtualModel() {
			if (getReceiver() != null && getReceiver().isSet()) {
				if (isAnalyzingContainer) {
					return null;
				}
				else {
					if (getReceiver().isValid()) {
						isAnalyzingContainer = true;
						Type vmiType = getReceiver().getAnalyzedType();
						isAnalyzingContainer = false;
						if (vmiType instanceof VirtualModelInstanceType) {
							return ((VirtualModelInstanceType) vmiType).getVirtualModel();
						}
					}
				}
			}
			// I could not find VM, trying to "guess" (TODO: remove this hack ?)
			if (getFlexoConcept() instanceof VirtualModel) {
				return (VirtualModel) getFlexoConcept();
			}
			if (getInferedModelSlot() != null) {
				return getInferedModelSlot().getAccessedVirtualModel();
			}
			return getOwningVirtualModel();
		}

		@Override
		public void setFlexoConceptType(FlexoConcept flexoConceptType) {
			if (flexoConceptType != this.flexoConceptType) {
				FlexoConcept oldValue = this.flexoConceptType;
				this.flexoConceptType = flexoConceptType;
				getPropertyChangeSupport().firePropertyChange("flexoConceptType", oldValue, oldValue);
			}
		}

		public HbnVirtualModelInstance getVirtualModelInstance(RunTimeEvaluationContext evaluationContext) {
			if (getReceiver() != null && getReceiver().isSet() && getReceiver().isValid()) {
				try {
					return getReceiver().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		public FlexoConceptInstance getContainer(RunTimeEvaluationContext evaluationContext) {
			if (getContainer() != null && getContainer().isSet() && getContainer().isValid()) {
				try {
					return getContainer().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public List<HbnFlexoConceptInstance> execute(RunTimeEvaluationContext evaluationContext) {

			System.out.println("On doit executer un PerformSQLQuery");

			HbnVirtualModelInstance vmi = getVirtualModelInstance(evaluationContext);
			FlexoConceptInstance container = getContainer(evaluationContext);

			if (container == null) {
				container = vmi;
			}

			if (vmi != null) {

				try {

					Query<?> sqlQ = vmi.getDefaultSession().createQuery("select o from " + getFlexoConceptType().getName() + " o");

					return vmi.getFlexoConceptInstances(sqlQ, container, getFlexoConceptType());

				} catch (HbnException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;

			}
			else {
				logger.warning(
						getStringRepresentation() + " : Cannot find virtual model instance on which to apply SelectFlexoConceptInstance");
				logger.warning("getReceiver()=" + getReceiver());
				/*logger.warning("evaluationContext=" + evaluationContext);
				logger.warning("isSet=" + getVirtualModelInstance().isSet());
				logger.warning("isValid=" + getVirtualModelInstance().isValid());
				logger.warning("fci=" + evaluationContext.getFlexoConceptInstance());
				logger.warning("vmi=" + evaluationContext.getVirtualModelInstance());
				try {
					logger.warning("value=" + getVirtualModelInstance().getBindingValue(evaluationContext));
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}*/
				logger.warning(getOwner().getFMLRepresentation());
				return null;
			}
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			super.notifiedBindingChanged(dataBinding);
			if (dataBinding == getReceiver()) {
				getPropertyChangeSupport().firePropertyChange("addressedVirtualModel", null, getAddressedVirtualModel());
			}
		}

	}
}
