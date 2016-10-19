package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCTableActorReference.JDBCTableActorReferenceImpl.class)
public interface JDBCTableActorReference extends JDBCActorReference<JDBCTable> {

	String TABLE_ID = "tableId";

	@Getter(TABLE_ID)
	String getTableId();

	@Setter(TABLE_ID)
	void setTableId(String newId);

	abstract class JDBCTableActorReferenceImpl extends JDBCActorReferenceImpl<JDBCTable> implements JDBCTableActorReference {

		private JDBCTable table;

		@Override
		public String getTableId() {
			if (table != null) return table.getName();
			return (String) performSuperGetter(TABLE_ID);
		}

		@Override
		public JDBCTable getModellingElement() {
			if (table == null) {
				String tableId = getTableId();
				table = ((JDBCConnection) getModelSlotInstance().getAccessedResourceData()).getSchema().getTable(tableId);
			}
			return table;
		}

		@Override
		public void setModellingElement(JDBCTable object) {
			if (table != object) {
				Object oldValue = table;
				table = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
