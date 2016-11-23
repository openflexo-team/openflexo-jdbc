package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCColumnActorReference.JDBCColumnActorReferenceImpl.class)
public interface JDBCColumnActorReference extends JDBCActorReference<JDBCColumn> {

	String TABLE_ID = "tableId";
	String COLUMN_ID = "columnId";

	@Getter(TABLE_ID) @XMLAttribute
	String getTableId();

	@Setter(TABLE_ID)
	void setTableId(String newId);

	@Getter(COLUMN_ID) @XMLAttribute
	String getColumnId();

	@Setter(COLUMN_ID)
	void setColumnId(String newId);

	abstract class JDBCColumnActorReferenceImpl extends JDBCActorReferenceImpl<JDBCColumn> implements JDBCColumnActorReference {

		private JDBCColumn column;

		@Override
		public String getTableId() {
			if (column != null) return column.getTable().getName();
			return (String) performSuperGetter(TABLE_ID);
		}

		@Override
		public String getColumnId() {
			if (column != null) return column.getName();
			return (String) performSuperGetter(COLUMN_ID);
		}

		@Override
		public JDBCColumn getModellingElement() {
			if (column == null) {
				String tableId = getTableId();
				JDBCTable table = getConnection().getSchema().getTable(tableId);
				column = table.getColumn(getColumnId());
			}
			return column;
		}

		@Override
		public void setModellingElement(JDBCColumn object) {
			if (column != object) {
				Object oldValue = column;
				column = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
