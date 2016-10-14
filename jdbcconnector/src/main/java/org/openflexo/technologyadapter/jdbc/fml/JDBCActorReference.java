package org.openflexo.technologyadapter.jdbc.fml;

import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * JDBC Actor reference
 */

@ModelEntity
@XMLElement
@ImplementationClass(JDBCActorReference.JDBCActorReferenceImpl.class)
@FML("JDBCActorReference")
public interface JDBCActorReference<T> extends ActorReference<T> {
	abstract class JDBCActorReferenceImpl<T> extends ActorReferenceImpl<T> implements JDBCActorReference<T>  {
	}
}
