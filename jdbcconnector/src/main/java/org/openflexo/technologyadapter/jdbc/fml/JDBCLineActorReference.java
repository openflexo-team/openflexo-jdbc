package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription;
import org.openflexo.technologyadapter.jdbc.model.JDBCValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor reference for a Table
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCLineActorReference.JDBCLineActorReferenceImpl.class)
public interface JDBCLineActorReference extends JDBCActorReference<JDBCLine> {

	String RESULTSET_DESCRIPTION = "resultsetDescription";

	String KEYS = "keys";

	@Getter(RESULTSET_DESCRIPTION) @XMLAttribute
	JDBCResultSetDescription getResultSetDescription();

	@Setter(RESULTSET_DESCRIPTION)
	void setResultSetDescription(JDBCResultSetDescription resultRestDescription);

	@Getter(value=KEYS, cardinality = Getter.Cardinality.LIST) @Embedded
	List<String> getKeys();

	@Adder(KEYS)
	void addToKeys(String key);

	@Remover(KEYS)
	void removeFromKeys(String key);

	abstract class JDBCLineActorReferenceImpl extends JDBCActorReferenceImpl<JDBCLine> implements JDBCLineActorReference {

		private JDBCLine line;

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			if (line != null) return line.getResultSet().getResultSetDescription();
			return (JDBCResultSetDescription) performSuperGetter(RESULTSET_DESCRIPTION);
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
				JDBCResultSetDescription description= getResultSetDescription();
				JDBCConnection connection = (JDBCConnection) getModelSlotInstance().getAccessedResourceData();
				JDBCResultSet resultSet = connection.select(description);
				line = resultSet.find(getKeys());
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
