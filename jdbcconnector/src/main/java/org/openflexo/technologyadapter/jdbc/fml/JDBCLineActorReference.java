package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCLineActorReference.JDBCLineActorReferenceImpl.class)
public interface JDBCLineActorReference extends JDBCActorReference<JDBCLine> {

	String TABLE_ID = "tableId";

	String KEYS = "keys";

	@Getter(TABLE_ID)
	String getTableId();

	@Setter(TABLE_ID)
	void setTableId(String newId);

	@Getter(value=KEYS, cardinality = Getter.Cardinality.LIST)
	List<String> getKeys();

	@Adder(KEYS)
	void addToKeys(String key);

	@Remover(KEYS)
	void removeFromKeys(String key);

	abstract class JDBCLineActorReferenceImpl extends JDBCActorReferenceImpl<JDBCLine> implements JDBCLineActorReference {

		private JDBCLine line;
		private Map<String, String> keys;

		@Override
		public String getTableId() {
			if (line != null) return line.getTable().getName();
			return (String) performSuperGetter(TABLE_ID);
		}

		@Override
		public List<String> getKeys() {
			if (line != null) {
				List<String> keys = new ArrayList<>();
				for (JDBCValue value : line.getValues()) {
					if (value.getColumn().isPrimaryKey()) {
						keys.add(value.getValue());
					}
				}
				return keys;
			}
			return (List<String>) performSuperGetter(KEYS);
		}

		@Override
		public JDBCLine getModellingElement() {
			if (line == null) {
				String tableId = getTableId();
				JDBCTable table = ((JDBCConnection) getModelSlotInstance().getAccessedResourceData()).getSchema().getTable(tableId);
				line = table.find(getKeys());
			}
			return line;
		}

		@Override
		public void setModellingElement(JDBCLine object) {
			if (line != object) {
				Object oldValue = line;
				line = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
