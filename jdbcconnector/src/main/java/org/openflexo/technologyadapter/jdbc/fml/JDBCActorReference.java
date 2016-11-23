package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;

/**
 * JDBC Actor reference
 */

@ModelEntity
@XMLElement
@ImplementationClass(JDBCActorReference.JDBCActorReferenceImpl.class)
@FML("JDBCActorReference")
public interface JDBCActorReference<T> extends ActorReference<T> {
	abstract class JDBCActorReferenceImpl<T> extends ActorReferenceImpl<T> implements JDBCActorReference<T>  {
		JDBCConnection getConnection() {
			ModelSlotInstance<?, ? extends ResourceData> modelSlotInstance = getResourceData().getModelSlotInstance("connection");
			return (JDBCConnection) modelSlotInstance.getAccessedResourceData();
		}
	}
}
