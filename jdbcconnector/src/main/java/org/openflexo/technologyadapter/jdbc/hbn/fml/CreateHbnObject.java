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

import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.editionaction.AbstractAddFlexoConceptInstance;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnException;
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
		public HbnFlexoConceptInstance execute(RunTimeEvaluationContext evaluationContext) {
			HbnVirtualModelInstance vmi = getVirtualModelInstance(evaluationContext);

			System.out.println("Hop, je dois creer un HbnFlexoConceptInstance, concept=" + getFlexoConceptType());

			try {
				// Transaction trans;
				// trans = vmi.getDefaultSession().beginTransaction();

				HbnFlexoConceptInstance returned = super.execute(evaluationContext);

				System.out.println("hop avec " + returned);

				// System.out.println("map=" + returned.getHbnSupportObject());

				// returned.getHbnSupportObject().put("ID_CARACTERISATION", new Integer(1));
				// returned.getHbnSupportObject().put("id_caracterisation", new Integer(1));

				/*for (Object o : returned.getHbnSupportObject().keySet()) {
					System.out.println(" > " + o + " = " + returned.getHbnSupportObject().get(o));
				}*/

				vmi.getDefaultSession().save(getFlexoConceptType().getName(), returned.getHbnSupportObject());

				// System.out.println("et on commite " + returned.getHbnSupportObject());

				// trans.commit();

				System.out.println("done " + returned);

				return returned;

			} catch (HbnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			/*Map<String, String> syl = new HashMap<>();
			syl.put("Nom", "Sylvain");
			Map<String, String> chris = new HashMap<>();
			chris.put("Nom", "Guychard");
			chris.put("Prenom", "Christophe");
			
			// Sérialisation de l'instance
			// Hibernate native
			Transaction trans = hbnSession.beginTransaction();
			
			hbnSession.save("Dynamic_Class", syl);
			hbnSession.save("Dynamic_Class", chris);
			
			trans.commit();*/

		}

		@Override
		protected HbnFlexoConceptInstance makeNewFlexoConceptInstance(RunTimeEvaluationContext evaluationContext) {
			FlexoConceptInstance container = null;
			HbnVirtualModelInstance vmi = getVirtualModelInstance(evaluationContext);

			if (getFlexoConceptType().getContainerFlexoConcept() != null) {
				container = getContainer(evaluationContext);
				if (container == null) {
					logger.warning("null container while creating new concept " + getFlexoConceptType());
					return null;
				}
			}

			return vmi.makeNewFlexoConceptInstance(getFlexoConceptType(), container);
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
