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
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;

import java.sql.Connection;

@ModelEntity
@ImplementationClass(value = JDBCConnectionImpl.class)
@XMLElement
@Imports({ @Import(JDBCSchema.class), @Import(JDBCTable.class), @Import(JDBCColumn.class)})
public interface JDBCConnection extends TechnologyObject<JDBCTechnologyAdapter>, ResourceData<JDBCConnection> {

    String ADDRESS = "address";
    String USER = "user";
    String PASSWORD = "password";
    String SCHEMA = "schema";
    String CONNECTION = "connection";

    @Getter(ADDRESS) @XMLAttribute
    String getAddress();

    @Setter(ADDRESS)
    void setAddress(String address);

    @Getter(USER) @XMLAttribute
    String getUser();

    @Setter(USER)
    void setUser(String user);

    @Getter(PASSWORD) @XMLAttribute
    String getPassword();

    @Setter(PASSWORD)
    void setPassword(String pass);

    @Getter(SCHEMA)
    JDBCSchema getSchema();

    @Getter(value = CONNECTION, ignoreType = true)
    Connection getConnection();
}
