package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;

/**
 * JDBC connector column description
 */
@ModelEntity
public interface JDBCColumn {

	String TABLE = "table";
	String PRIMARY_KEY = "primaryKey";
	String NAME = "name";
	String TYPE = "type";

	@Initializer void init(
		@Parameter(TABLE) JDBCTable table, @Parameter(PRIMARY_KEY) boolean primaryKey,
		@Parameter(NAME) String name, @Parameter(TYPE) String type
	);

	@Getter(TABLE) JDBCTable getTable();

	@Getter(value = PRIMARY_KEY, defaultValue = "false")
	boolean isPrimaryKey();

	@Getter(NAME) String getName();

	@Getter(TYPE) String getType();

}
