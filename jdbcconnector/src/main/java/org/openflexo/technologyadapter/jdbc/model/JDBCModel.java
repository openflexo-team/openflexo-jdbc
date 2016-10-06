/*
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.jdbc.model;


import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

import java.sql.Connection;
import java.sql.SQLException;

@ModelEntity
@ImplementationClass(value = JDBCModelImpl.class)
@XMLElement
@Imports(@Import(JDBCSchema.class))
public interface JDBCModel extends TechnologyObject<JDBCTechnologyAdapter>, ResourceData<JDBCModel>{

    String ADDRESS = "address";
    String USER = "user";
    String PASSWORD = "password";

    @Initializer
    void init(@Parameter(ADDRESS) String address,
			  @Parameter(USER) String user,
			  @Parameter(PASSWORD) String password);

    @Getter(ADDRESS) @XMLAttribute
    String getAddress();

    @Getter(USER) @XMLAttribute
    String getUser();

    @Getter(PASSWORD) @XMLAttribute
    String getPassword();

    JDBCSchema getSchema() throws SQLException;

    Connection getConnection() throws SQLException;
}
