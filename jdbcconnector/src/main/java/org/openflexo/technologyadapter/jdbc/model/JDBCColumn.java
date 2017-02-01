package org.openflexo.technologyadapter.jdbc.model;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InnerResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;

import java.util.Objects;

/**
 * JDBC connector column description
 */
@ModelEntity
@ImplementationClass(JDBCColumn.JDBCColumnImpl.class)
public interface JDBCColumn extends FlexoObject, InnerResourceData<JDBCConnection> {

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

	abstract class JDBCColumnImpl extends FlexoObjectImpl implements JDBCColumn {

		@Override
		public JDBCConnection getResourceData() {
			return getTable().getSchema().getModel();
		}

		public boolean equals(Object other) {
			if (other instanceof JDBCColumn) {
				JDBCColumn two = (JDBCColumn) other;
				if (!Objects.equals(getName(), two.getName())) return false;
				if (getTable() == null) return two.getTable() == null;
				return Objects.equals(getTable().getName(), two.getTable().getName());
			}
			return false;
		}
	}
}
