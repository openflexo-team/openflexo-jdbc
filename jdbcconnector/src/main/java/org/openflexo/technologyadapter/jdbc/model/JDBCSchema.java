package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;

import java.util.List;

/**
 * JDBC Schema. It contains table description for the connected JDBC resource.
 */
@ModelEntity
@ImplementationClass(JDBCSchemaImpl.class)
public interface JDBCSchema {

    String TABLES = "tables";

    @Getter(value = TABLES, cardinality = Cardinality.LIST)
    List<JDBCTable> getTables();

    @Setter(TABLES)
    void setTables(List<JDBCTable> tables);

    @Adder(TABLES)
    void addToTables(JDBCTable aTable);

    @Remover(TABLES)
    void removeFromTables(JDBCTable aTable);
}
