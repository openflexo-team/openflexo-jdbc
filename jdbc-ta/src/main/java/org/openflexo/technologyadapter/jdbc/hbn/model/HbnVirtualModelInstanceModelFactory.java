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

package org.openflexo.technologyadapter.jdbc.hbn.model;

import java.util.Map;

import org.openflexo.foundation.fml.AbstractCreationScheme;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedVirtualModelInstanceModelFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

/**
 * {@link PamelaModelFactory} used to handle {@link HbnVirtualModelInstance} models<br>
 * 
 * @author sylvain
 * 
 */
public class HbnVirtualModelInstanceModelFactory
		extends ReflectedVirtualModelInstanceModelFactory<JDBCResource, JDBCConnection, JDBCTechnologyAdapter, Map<String, Object>> {

	public HbnVirtualModelInstanceModelFactory(JDBCResource resource, EditingContext editingContext, TechnologyAdapterService taService)
			throws ModelDefinitionException {
		super(resource, HbnVirtualModelInstance.class, editingContext, taService);
	}

	@Override
	public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, Map<String, Object> supportObject,
			FlexoConceptInstance container, VirtualModelInstance<?, ?> ownerVirtualModelInstance,
			RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {
		return (HbnFlexoConceptInstance) super.makeNewFlexoConceptInstance(concept, supportObject, container, ownerVirtualModelInstance,
				evaluationContext);
	}

	@Override
	public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, Map<String, Object> supportObject,
			FlexoConceptInstance container, VirtualModelInstance<?, ?> ownerVirtualModelInstance, AbstractCreationScheme creationScheme,
			RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {
		HbnFlexoConceptInstance returned = newInstance(HbnFlexoConceptInstance.class, supportObject, concept);
		ownerVirtualModelInstance.addToFlexoConceptInstances(returned);
		if (container != null && container != ownerVirtualModelInstance) {
			container.addToEmbeddedFlexoConceptInstances(returned);
		}
		return returned;
	}

}
