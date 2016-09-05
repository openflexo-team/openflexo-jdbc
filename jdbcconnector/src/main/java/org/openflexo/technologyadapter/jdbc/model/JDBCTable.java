package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

import java.util.List;

/**
 * JDBC connector table description
 */
@ModelEntity
public interface JDBCTable {

    String NAME = "name";
    String COLUMNS = "columns";

    @Getter(NAME)
    String getName();

    @Setter(NAME)
    void setName(String name);

    @Getter(value = COLUMNS, cardinality = Cardinality.LIST)
    List<JDBCTable> getColumns();

    @Setter(COLUMNS)
    void setColumns(List<JDBCColumn> columns);

    @Adder(COLUMNS)
    void addToColumns(JDBCColumn aColumn);

    @Remover(COLUMNS)
    void removeFromColumns(JDBCColumn aColumn);

}
