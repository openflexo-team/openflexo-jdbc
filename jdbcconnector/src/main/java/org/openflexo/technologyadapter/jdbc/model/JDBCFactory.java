/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.foundation.PamelaResourceModelFactory;
import org.openflexo.model.converter.RelativePathResourceConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.EditingContext;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;

import java.util.logging.Logger;

/**
 * one JDBFa
 * 
 * @author charlie
 * 
 */
public class JDBCFactory extends FGEModelFactoryImpl implements PamelaResourceModelFactory<JDBCResource> {

	private static final Logger logger = Logger.getLogger(JDBCFactory.class.getPackage().getName());

	private final JDBCResource resource;

	public JDBCFactory(JDBCResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(JDBCModel.class);
		this.resource = resource;
		setEditingContext(editingContext);
		if(resource!=null){
			addConverter(new RelativePathResourceConverter(resource));
		}
	}

	@Override
	public JDBCResource getResource() {
		return resource;
	}

	/**
	 * Creates empty model that needs to be initialized
	 * @return the created model
	 */
	public JDBCModel makeEmptyModel() {
		return newInstance(JDBCModel.class);
	}

	public JDBCModel makeNewModel(String address, String user, String password) {
		JDBCModel returned = newInstance(JDBCModel.class);
		returned.setAddress(address);
		returned.setUser(user);
		returned.setPassword(password);
		return returned;
	}

	@Override
	public void startDeserializing() {
		System.out.println("[JDBC] start deserializer");
	}

	@Override
	public void stopDeserializing() {
		System.out.println("[JDBC] stop deserializer");
	}
}
