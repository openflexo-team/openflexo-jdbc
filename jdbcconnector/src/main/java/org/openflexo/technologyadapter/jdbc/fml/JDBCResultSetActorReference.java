package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.fml.JDBCResultSetActorReference.JDBCResultSetActorReferenceImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSetDescription;

/**
 * Actor reference for a ResultSet
 */
@ModelEntity
@XMLElement
@ImplementationClass(JDBCResultSetActorReferenceImpl.class)
public interface JDBCResultSetActorReference extends JDBCActorReference<JDBCResultSet> {

	String RESULTSET_DESCRIPTION = "requestDescription";

	@Getter(RESULTSET_DESCRIPTION) @Embedded
	JDBCResultSetDescription getResultSetDescription();

	@Setter(RESULTSET_DESCRIPTION)
	void setResultSetDescription(JDBCResultSetDescription newDescription);

	abstract class JDBCResultSetActorReferenceImpl extends JDBCActorReferenceImpl<JDBCResultSet> implements JDBCResultSetActorReference {

		private JDBCResultSet resultSet;

		@Override
		public JDBCResultSetDescription getResultSetDescription() {
			if (resultSet != null) return resultSet.getResultSetDescription();
			return (JDBCResultSetDescription) performSuperGetter(RESULTSET_DESCRIPTION);
		}

		@Override
		public JDBCResultSet getModellingElement() {
			if (resultSet == null) {
				resultSet = getConnection().select(getResultSetDescription());
			}
			return resultSet;
		}

		@Override
		public void setModellingElement(JDBCResultSet object) {
			if (resultSet != object) {
				Object oldValue = resultSet;
				resultSet = object;
				getPropertyChangeSupport().firePropertyChange(MODELLING_ELEMENT_KEY, oldValue, object);
			}
		}
	}

}
