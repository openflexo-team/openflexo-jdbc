package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;

/**
 * A value inside a JDBCLine
 */
@ModelEntity
public interface JDBCValue {

	String COLUMN = "column";
	String VALUE = "value";

	@Initializer
	void init(@Parameter(COLUMN) JDBCColumn column, @Parameter(VALUE) String value);

	@Getter(COLUMN)
	JDBCColumn getColumn();

	@Getter(VALUE)
	String getValue();
}
