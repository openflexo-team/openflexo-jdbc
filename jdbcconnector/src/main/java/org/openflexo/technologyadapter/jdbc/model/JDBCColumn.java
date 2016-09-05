package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

/**
 * JDBC connector column description
 */
@ModelEntity
public interface JDBCColumn {

    String NAME = "name";
    String TYPE = "type";

    @Getter(NAME)
    String getName();

    @Setter(NAME)
    void setName(String name);

    @Getter(TYPE)
    String getType();

    @Setter(TYPE)
    void setType(String type);

}
