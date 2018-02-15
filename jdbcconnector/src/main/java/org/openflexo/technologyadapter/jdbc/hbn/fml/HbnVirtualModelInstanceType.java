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

import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;

/**
 * Represent the type of an instance of a {@link VirtualModel} in the context of {@link HbnModelSlot}
 * 
 * @author sylvain
 * 
 */
public class HbnVirtualModelInstanceType extends VirtualModelInstanceType {

	public static HbnVirtualModelInstanceType UNDEFINED_VIRTUAL_MODEL_INSTANCE_TYPE = new HbnVirtualModelInstanceType((VirtualModel) null);

	private HbnVirtualModelInstanceType(VirtualModel aVirtualModel) {
		super(aVirtualModel);
	}

	public HbnVirtualModelInstanceType(String virtualModelURI, CustomTypeFactory<?> factory) {
		super(virtualModelURI, factory);
	}

	@Override
	public Class<?> getBaseClass() {
		return HbnVirtualModelInstance.class;
	}

	private static Map<VirtualModel, HbnVirtualModelInstanceType> types = new HashedMap<>();

	public static HbnVirtualModelInstanceType getVirtualModelInstanceType(VirtualModel aVirtualModel) {
		if (aVirtualModel != null) {
			HbnVirtualModelInstanceType returned = types.get(aVirtualModel);
			if (returned == null) {
				returned = new HbnVirtualModelInstanceType(aVirtualModel);
				types.put(aVirtualModel, returned);
			}
			return returned;
		}
		// logger.warning("Trying to get a VirtualModelInstanceType for a null VirtualModel");
		return UNDEFINED_VIRTUAL_MODEL_INSTANCE_TYPE;
	}

	/**
	 * Factory for {@link VirtualModelInstanceType} instances
	 * 
	 * @author sylvain
	 * 
	 */
	public static class HbnVirtualModelInstanceTypeFactory extends AbstractVirtualModelInstanceTypeFactory<HbnVirtualModelInstanceType> {

		public HbnVirtualModelInstanceTypeFactory(JDBCTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@Override
		public Class<HbnVirtualModelInstanceType> getCustomType() {
			return HbnVirtualModelInstanceType.class;
		}

		@Override
		public HbnVirtualModelInstanceType getType(String configuration, CustomTypeFactory<?> factory) {
			return new HbnVirtualModelInstanceType(configuration, this);
		}

		@Override
		public HbnVirtualModelInstanceType getType(VirtualModel virtualModel) {
			return getVirtualModelInstanceType(virtualModel);
		}

	}

}
