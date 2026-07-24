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

package org.openflexo.technologyadapter.jdbc.rm;

import org.apache.commons.io.FilenameUtils;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCFactory;

/**
 *
 */
public class JDBCResourceFactory
		extends TechnologySpecificPamelaResourceFactory<JDBCResource, JDBCConnection, JDBCTechnologyAdapter, JDBCFactory> {

	public static final String JDBC_EXTENSION = "jdbc";

	public JDBCResourceFactory() throws ModelDefinitionException {
		super(JDBCResource.class);
	}

	@Override
	public JDBCFactory makeModelFactory(JDBCResource resource, TechnologyContextManager<JDBCTechnologyAdapter> technologyContextManager)
			throws ModelDefinitionException {
		return new JDBCFactory(resource, technologyContextManager.getServiceManager().getEditingContext());
	}

	@Override
	public JDBCConnection makeEmptyResourceData(JDBCResource resource) {
		return resource.getFactory().makeEmptyModel();
	}

	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		String name = resourceCenter.retrieveName(serializationArtefact);
		return FilenameUtils.isExtension(name, JDBC_EXTENSION);
	}

	public <I> JDBCResource makeJDBCResource(String baseName, RepositoryFolder<JDBCResource, I> folder)
			throws SaveResourceException, ModelDefinitionException {

		FlexoResourceCenter<I> rc = folder.getResourceRepository().getResourceCenter();
		String artefactName = baseName.endsWith(JDBC_EXTENSION) ? baseName : baseName + JDBC_EXTENSION;
		I serializationArtefact = rc.createEntry(artefactName, folder.getSerializationArtefact());
		JDBCResource newJDBCResource = makeResource(serializationArtefact, rc, true);

		return newJDBCResource;
	}
}
