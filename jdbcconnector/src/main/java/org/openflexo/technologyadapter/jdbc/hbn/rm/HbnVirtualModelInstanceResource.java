/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.jdbc.hbn.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.InconsistentDataException;
import org.openflexo.foundation.InvalidModelDefinitionException;
import org.openflexo.foundation.InvalidXMLException;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.AbstractVirtualModelInstanceResource;
import org.openflexo.foundation.fml.rt.rm.AbstractVirtualModelInstanceResourceImpl;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FlexoFileNotFoundException;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance;
import org.openflexo.toolbox.IProgress;

/**
 * This is the {@link FlexoResource} encoding a {@link FMLRTVirtualModelInstance}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(HbnVirtualModelInstanceResource.HbnVirtualModelInstanceResourceImpl.class)
@XMLElement
public interface HbnVirtualModelInstanceResource
		extends AbstractVirtualModelInstanceResource<HbnVirtualModelInstance, JDBCTechnologyAdapter>,
		FlexoModelResource<HbnVirtualModelInstance, VirtualModel, JDBCTechnologyAdapter, FMLTechnologyAdapter> {

	/**
	 * Default implementation for {@link HbnVirtualModelInstanceResource}
	 * 
	 * 
	 * @author Sylvain
	 * 
	 */
	public abstract class HbnVirtualModelInstanceResourceImpl
			extends AbstractVirtualModelInstanceResourceImpl<HbnVirtualModelInstance, JDBCTechnologyAdapter>
			implements HbnVirtualModelInstanceResource {

		static final Logger logger = Logger.getLogger(HbnVirtualModelInstanceResourceImpl.class.getPackage().getName());

		@Override
		public JDBCTechnologyAdapter getTechnologyAdapter() {
			if (getServiceManager() != null) {
				return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JDBCTechnologyAdapter.class);
			}
			return null;
		}

		@Deprecated
		@Override
		public Resource getDirectory() {
			String parentPath = getDirectoryPath();
			if (ResourceLocator.locateResource(parentPath) == null) {
				FileSystemResourceLocatorImpl.appendDirectoryToFileSystemResourceLocator(parentPath);
			}
			return ResourceLocator.locateResource(parentPath);
		}

		@Deprecated
		public String getDirectoryPath() {
			if (getIODelegate() instanceof FileIODelegate) {
				FileIODelegate ioDelegate = (FileIODelegate) getIODelegate();
				return ioDelegate.getFile().getParentFile().getAbsolutePath();
			}
			return "";
		}

		@Override
		public HbnVirtualModelInstance getModelData() {
			return getVirtualModelInstance();
		}

		@Override
		public HbnVirtualModelInstance getModel() {
			return getVirtualModelInstance();
		}

		@Override
		public String computeDefaultURI() {
			if (getContainer() != null) {
				return getContainer().getURI() + "/" + (getName().endsWith(getSuffix()) ? getName() : getName() + getSuffix());
			}
			if (getResourceCenter() != null) {
				return getResourceCenter().getDefaultBaseURI() + "/"
						+ (getName().endsWith(getSuffix()) ? getName() : getName() + getSuffix());
			}
			return null;
		}

		@Override
		public Class<JDBCTechnologyAdapter> getTechnologyAdapterClass() {
			return JDBCTechnologyAdapter.class;
		}

		private String virtualModelURI;

		@Override
		public String getVirtualModelURI() {
			if (getVirtualModelResource() != null) {
				return getVirtualModelResource().getURI();
			}
			return virtualModelURI;
		}

		@Override
		public void setVirtualModelURI(String virtualModelURI) {
			this.virtualModelURI = virtualModelURI;
		}

		public String getSuffix() {
			return HbnVirtualModelInstanceResourceFactory.JDBC_HBN_SUFFIX;
		}

		@Override
		public Class<HbnVirtualModelInstance> getResourceDataClass() {
			return HbnVirtualModelInstance.class;
		}

		@Override
		public HbnVirtualModelInstance loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException,
				InvalidXMLException, InconsistentDataException, InvalidModelDefinitionException {
			HbnVirtualModelInstance returned = super.loadResourceData(progress);
			// returned.setSupportFactory(new JsonSupportFactory("url"));
			try {
				returned.connectToDB();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return returned;
		}

	}

}
