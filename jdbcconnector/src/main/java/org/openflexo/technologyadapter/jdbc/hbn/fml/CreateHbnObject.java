/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.editionaction.AbstractAddFlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnFlexoConceptInstance;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * Create a new object as defined by an Hibernate mapping. This object will be an HbnFlexoConceptInstance
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(CreateHbnObject.CreateHbnObjectImpl.class)
@XMLElement
public interface CreateHbnObject extends AbstractAddFlexoConceptInstance<HbnFlexoConceptInstance, HbnVirtualModelInstance> {

	public static abstract class CreateHbnObjectImpl
			extends AbstractAddFlexoConceptInstanceImpl<HbnFlexoConceptInstance, HbnVirtualModelInstance> implements CreateHbnObject {

		private static final Logger logger = Logger.getLogger(CreateHbnObject.class.getPackage().getName());

		@Override
		public HbnFlexoConceptInstance execute(RunTimeEvaluationContext evaluationContext) throws FlexoException {
			HbnVirtualModelInstance vmi = getVirtualModelInstance(evaluationContext);

			System.out.println("CreateHbnObject for receiver " + getReceiver() + " = " + vmi + " concept=" + getFlexoConceptType());

			HbnFlexoConceptInstance returned = super.execute(evaluationContext);

			return returned;

		}

		@Override
		protected HbnFlexoConceptInstance makeNewFlexoConceptInstance(RunTimeEvaluationContext evaluationContext) throws FlexoException {
			FlexoConceptInstance container = null;
			HbnVirtualModelInstance vmi = getVirtualModelInstance(evaluationContext);

			if (getFlexoConceptType().getContainerFlexoConcept() != null) {
				container = getContainer(evaluationContext);
				if (container == null) {
					logger.warning("null container while creating new concept " + getFlexoConceptType());
					return null;
				}
			}

			HbnFlexoConceptInstance returned = vmi.makeNewFlexoConceptInstance(getFlexoConceptType(), container);

			/*try {
				// Note that we immediately save the created object in Hibernate session
				// This means that no NOT NULL values should be declared in related table
				// We do that to be able to chain hibernate objects creation
				// (we could also do it in execute(), but happen AFTER creation scheme, and sometimes it's too late)
				// TODO: a good alternative could be to expose SAVE primitive and use it in CreationScheme to control save
				// No time yet to do it
				vmi.getDefaultSession().save(getFlexoConceptType().getName(), returned.getHbnSupportObject());
			} catch (HbnException e) {
				throw new FlexoException(e);
			}*/

			return returned;
		}

		@Override
		public TechnologyAdapter getModelSlotTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return super.getModelSlotTechnologyAdapter();
		}
	}

}
