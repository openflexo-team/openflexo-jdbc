/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.jdbc;

import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareResourceFactory;
import org.openflexo.foundation.fml.rt.InferedFMLRTModelSlot;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnVirtualModelInstanceType;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnVirtualModelInstanceType.HbnVirtualModelInstanceTypeFactory;
import org.openflexo.technologyadapter.jdbc.hbn.rm.HbnVirtualModelInstanceRepository;
import org.openflexo.technologyadapter.jdbc.hbn.rm.HbnVirtualModelInstanceResourceFactory;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResourceFactory;

/**
 * This class defines and implements the JDBC technology adapter
 * 
 * @author charlie, sylvain
 * 
 */
@DeclareModelSlots({ JDBCModelSlot.class, HbnModelSlot.class })
@DeclareResourceFactory({ JDBCResourceFactory.class, HbnVirtualModelInstanceResourceFactory.class })
public class JDBCTechnologyAdapter extends TechnologyAdapter<JDBCTechnologyAdapter> {

	public JDBCTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getIdentifier() {
		return "JDBC";
	}

	@Override
	public String getName() {
		return "JDBC Technology Adapter";
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/JDBCTechnologyAdapter";
	}

	@Override
	public JDBCTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new JDBCTechnologyContextManager(this, service);
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub
		return false;
	}

	public <I> HbnVirtualModelInstanceRepository<I> getVirtualModelInstanceRepository(FlexoResourceCenter<I> resourceCenter) {
		HbnVirtualModelInstanceRepository<I> returned = resourceCenter.retrieveRepository(HbnVirtualModelInstanceRepository.class, this);
		if (returned == null) {
			returned = HbnVirtualModelInstanceRepository.instanciateNewRepository(this, resourceCenter);
			resourceCenter.registerRepository(returned, HbnVirtualModelInstanceRepository.class, this);
		}
		return returned;
	}

	@Override
	public VirtualModelInstanceType getInferedVirtualModelInstanceType(VirtualModel vm,
			Class<? extends InferedFMLRTModelSlot<?, ?>> modelSlotNatureClass) {
		if (HbnModelSlot.class.equals(modelSlotNatureClass)) {
			return HbnVirtualModelInstanceType.getVirtualModelInstanceType(vm);
		}
		return super.getInferedVirtualModelInstanceType(vm, modelSlotNatureClass);
	}

	@Override
	public void initTechnologySpecificTypes(TechnologyAdapterService taService) {
		super.initTechnologySpecificTypes(taService);
		taService.registerTypeClass(HbnVirtualModelInstanceType.class, getVirtualModelInstanceTypeFactory());
	}

	private HbnVirtualModelInstanceTypeFactory hbnVmiFactory;

	public HbnVirtualModelInstanceTypeFactory getVirtualModelInstanceTypeFactory() {
		if (hbnVmiFactory == null) {
			hbnVmiFactory = new HbnVirtualModelInstanceTypeFactory(this);
		}
		return hbnVmiFactory;
	}

}
