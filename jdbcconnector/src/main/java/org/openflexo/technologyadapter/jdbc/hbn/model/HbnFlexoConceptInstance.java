/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.technologyadapter.jdbc.hbn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hibernate.collection.internal.PersistentBag;
import org.openflexo.foundation.fml.AbstractProperty;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnColumnRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnOneToManyReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;

/**
 * A JDBC/Hibernate-specific {@link FlexoConceptInstance} reflecting a distant object accessible in an {@link HbnVirtualModelInstance}
 * through a {@link HbnModelSlot}<br>
 * 
 * This {@link HbnFlexoConceptInstance} internally manages Hibernate support object (a {@link Map}) which encodes mapped dynamic object
 * 
 */
@ModelEntity
@ImplementationClass(HbnFlexoConceptInstance.HbnFlexoConceptInstanceImpl.class)
@XMLElement
public interface HbnFlexoConceptInstance extends FlexoConceptInstance {

	/**
	 * Return a {@link Serializable} object which acts as key identifier for this {@link HbnFlexoConceptInstance}
	 * 
	 * <ul>
	 * <li>If key of declaring {@link FlexoConcept} is simple, just return the value of key property</li>
	 * <li>If Key is composite, return an Object array with all the values of properties composing composite key, in the order where those
	 * properties are declared in keyProperties of related {@link FlexoConcept}</li>
	 * </ul>
	 * 
	 * @param hbnMap
	 * @param concept
	 * @return
	 */

	public Object getIdentifier();

	/**
	 * Return a {@link String} object which acts as string representation for this {@link HbnFlexoConceptInstance}
	 * 
	 * <ul>
	 * <li>If key of declaring {@link FlexoConcept} is simple, just return the String value of key property</li>
	 * <li>If Key is composite, return an comma-separated dictionary as a String with all the values of properties composing composite key,
	 * in the order where those properties are declared in keyProperties of related {@link FlexoConcept}</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getIdentifierAsString();

	/**
	 * Initialize this {@link HbnFlexoConceptInstance} with supplied Hibernate support object, and explicit concept (type)
	 * 
	 * @param hbnMap
	 * @param concept
	 */
	@Initializer
	void initialize(Map<String, Object> hbnMap, FlexoConcept concept);

	/**
	 * Return Hibernate support object (a map)
	 * 
	 * @return
	 */
	public Map<String, Object> getHbnSupportObject();

	/**
	 * Re-read the state of the given instance from the underlying database. It is inadvisable to use this to implement long-running
	 * sessions that span many business tasks. This action is, however, useful in certain special circumstances. For example
	 * <ul>
	 * <li>where a database trigger alters the object state upon insert or update
	 * <li>afterQuery executing direct SQL (eg. a mass update) in the same session
	 * <li>afterQuery inserting a <tt>Blob</tt> or <tt>Clob</tt>
	 * </ul>
	 *
	 * @throws HbnException
	 */
	public void refresh() throws HbnException;

	/**
	 * Default implementation for {@link HbnFlexoConceptInstance}
	 * 
	 * @author sylvain
	 *
	 */
	abstract class HbnFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements HbnFlexoConceptInstance {

		private static final Logger logger = FlexoLogger.getLogger(HbnFlexoConceptInstance.class.getPackage().toString());

		// Hibernate support object
		private Map<String, Object> hbnMap;

		private Serializable identifier = null;
		private String identifierAsString = null;

		// This map stores references for this object
		// TODO: support modification
		private Map<HbnToOneReferenceRole, HbnFlexoConceptInstance> referencedMap = new HashMap<>();
		private Map<HbnOneToManyReferenceRole, HbnReferenceCollection> referencedCollectionsMap = new HashMap<>();

		/**
		 * Initialize this {@link HbnFlexoConceptInstance} with supplied Hibernate support object, and explicit concept (type)
		 * 
		 * @param hbnMap
		 * @param concept
		 */
		@Override
		public void initialize(Map<String, Object> hbnMap, FlexoConcept concept) {
			this.hbnMap = hbnMap;
			setFlexoConcept(concept);
		}

		@Override
		public HbnVirtualModelInstance getVirtualModelInstance() {
			return (HbnVirtualModelInstance) super.getVirtualModelInstance();
		}

		@Override
		public Map<String, Object> getHbnSupportObject() {
			return hbnMap;
		}

		private HbnFlexoConceptInstance getReferencedObject(HbnToOneReferenceRole referenceRole) {
			// TODO: support modification !!!
			// With this implementation, we will always return cached value
			HbnFlexoConceptInstance returned = referencedMap.get(referenceRole);
			if (returned == null) {
				Map<String, Object> refHbnMap = (Map<String, Object>) hbnMap.get(referenceRole.getName());
				if (refHbnMap != null) {
					returned = getVirtualModelInstance().getFlexoConceptInstance(refHbnMap, null, referenceRole.getFlexoConceptType());
					if (returned != null) {
						referencedMap.put(referenceRole, returned);
					}
				}
			}
			return returned;
		}

		private void setReferencedObject(HbnFlexoConceptInstance newValue, HbnToOneReferenceRole referenceRole) {
			HbnFlexoConceptInstance oldValue = getReferencedObject(referenceRole);
			if (oldValue != newValue) {
				hbnMap.put(referenceRole.getName(), newValue.getHbnSupportObject());
				identifier = null;
				identifierAsString = null;
				referencedMap.remove(referenceRole);
				setIsModified();
				getPropertyChangeSupport().firePropertyChange(referenceRole.getPropertyName(), oldValue, newValue);
			}

		}

		/**
		 * Wrapper for a collection of objects accessed though a HbnOneToManyReferenceRole<br>
		 * 
		 * Internally wrap a {@link PersistentBag} object
		 * 
		 * @author sylvain
		 *
		 */
		// TODO improve instances list updating when objects change
		public class HbnReferenceCollection {

			private PersistentBag pBag;
			private final HbnOneToManyReferenceRole referenceRole;
			private List<HbnFlexoConceptInstance> instances = null;

			private boolean isRefreshing = false;

			public HbnReferenceCollection(HbnOneToManyReferenceRole referenceRole) {
				this.pBag = (PersistentBag) hbnMap.get(referenceRole.getName());
				this.referenceRole = referenceRole;
			}

			public List<HbnFlexoConceptInstance> getInstances() {

				if (needsRefresh()) {
					refresh();
				}

				if (instances == null) {
					instances = new ArrayList<HbnFlexoConceptInstance>() {
						@Override
						public boolean add(HbnFlexoConceptInstance e) {
							if (!isRefreshing) {
								// TODO: handle this
								// today, we can only set opposite property
								logger.warning("ADDING not implemented yet");
								return false;
							}
							return super.add(e);
						}
					};

					if (pBag != null) {
						isRefreshing = true;
						for (Object o : pBag) {
							if (o instanceof Map) {
								HbnFlexoConceptInstance fci = getVirtualModelInstance().getFlexoConceptInstance((Map<String, Object>) o,
										null, referenceRole.getFlexoConceptType());
								instances.add(fci);
							}
						}
						isRefreshing = false;
					}
				}

				return instances;
			}

			public boolean needsRefresh() {
				return (instances != null && pBag != null && instances.size() != pBag.size());
			}

			public void refresh() {
				this.pBag = (PersistentBag) hbnMap.get(referenceRole.getName());
				instances = null;
			}
		}

		@Override
		public void refresh() throws HbnException {

			// Hibernate refresh
			getVirtualModelInstance().getDefaultSession().refresh(getFlexoConcept().getName(), (Object) getHbnSupportObject());

			// We need now to refresh all HbnReferenceCollection
			for (HbnReferenceCollection refCol : referencedCollectionsMap.values()) {
				refCol.refresh();
			}

		}

		private List<HbnFlexoConceptInstance> getReferencedObjectList(HbnOneToManyReferenceRole referenceRole) {
			HbnReferenceCollection referenceCollection = referencedCollectionsMap.get(referenceRole);
			if (referenceCollection == null) {
				// PersistentBag pBag = (PersistentBag) hbnMap.get(referenceRole.getName());
				/*if (pBag == null) {
					return Collections.emptyList();
				}*/
				referenceCollection = new HbnReferenceCollection(referenceRole);
				referencedCollectionsMap.put(referenceRole, referenceCollection);
			}
			return referenceCollection.getInstances();
		}

		@Override
		public <T> T getFlexoActor(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof HbnColumnRole) {
				T returned = (T) hbnMap.get(flexoRole.getName());
				return returned;
			}
			else if (flexoRole instanceof HbnToOneReferenceRole) {
				return (T) getReferencedObject((HbnToOneReferenceRole) flexoRole);
			}
			return super.getFlexoActor(flexoRole);
		}

		@Override
		public <T> void setFlexoActor(T object, FlexoRole<T> flexoRole) {
			if (flexoRole instanceof HbnColumnRole) {
				T oldValue = getFlexoActor(flexoRole);
				if ((object == null && oldValue != null) || (object != null && !object.equals(oldValue))) {
					Object objectToBeStored = ((HbnColumnRole<?>) flexoRole).getDataType().encodeObjectForStoring(object);
					hbnMap.put(flexoRole.getName(), objectToBeStored);
					identifier = null;
					identifierAsString = null;
					setIsModified();
					getPropertyChangeSupport().firePropertyChange(flexoRole.getPropertyName(), oldValue, object);
				}
			}
			else if (flexoRole instanceof HbnToOneReferenceRole) {
				setReferencedObject((HbnFlexoConceptInstance) object, (HbnToOneReferenceRole) flexoRole);
			}
			else {
				super.setFlexoActor(object, flexoRole);
			}
		}

		@Override
		public <T> List<T> getFlexoActorList(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof HbnOneToManyReferenceRole) {
				return (List<T>) getReferencedObjectList((HbnOneToManyReferenceRole) flexoRole);
			}
			return super.getFlexoActorList(flexoRole);
		}

		@Override
		public <T> T getFlexoPropertyValue(FlexoProperty<T> flexoProperty) {
			// Deprecated
			if (flexoProperty instanceof AbstractProperty) {
				T returned = (T) hbnMap.get(flexoProperty.getName());
				return returned;
			}
			// End deprecated

			if (flexoProperty instanceof HbnColumnRole) {
				T returned = (T) hbnMap.get(flexoProperty.getName());
				return returned;
			}
			if (flexoProperty instanceof HbnToOneReferenceRole) {
				return (T) getReferencedObject((HbnToOneReferenceRole) flexoProperty);
			}
			if (flexoProperty instanceof HbnOneToManyReferenceRole) {
				return (T) getReferencedObjectList((HbnOneToManyReferenceRole) flexoProperty);
			}

			return super.getFlexoPropertyValue(flexoProperty);
		}

		@Override
		public <T> void setFlexoPropertyValue(FlexoProperty<T> flexoProperty, T value) {
			// Deprecated
			if (flexoProperty instanceof AbstractProperty) {
				T oldValue = getFlexoPropertyValue(flexoProperty);
				if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue))) {
					hbnMap.put(flexoProperty.getName(), value);
					identifier = null;
					identifierAsString = null;
					setIsModified();
					getPropertyChangeSupport().firePropertyChange(flexoProperty.getPropertyName(), oldValue, value);
				}
			}
			// End deprecated
			else if (flexoProperty instanceof HbnColumnRole) {
				T oldValue = getFlexoPropertyValue(flexoProperty);
				if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue))) {
					Object objectToBeStored = ((HbnColumnRole<?>) flexoProperty).getDataType().encodeObjectForStoring(value);
					hbnMap.put(flexoProperty.getName(), objectToBeStored);
					identifier = null;
					identifierAsString = null;
					setIsModified();
					getPropertyChangeSupport().firePropertyChange(flexoProperty.getPropertyName(), oldValue, value);
				}

			}
			else if (flexoProperty instanceof HbnToOneReferenceRole) {
				setReferencedObject((HbnFlexoConceptInstance) value, (HbnToOneReferenceRole) flexoProperty);
			}
			else {
				super.setFlexoPropertyValue(flexoProperty, value);
			}
		}

		@Override
		public Serializable getIdentifier() {
			if (getFlexoConcept() == null) {
				return null;
			}
			if (identifier == null) {
				identifier = getVirtualModelInstance().getIdentifier(hbnMap, getFlexoConcept());
			}
			return identifier;
		}

		@Override
		public String getIdentifierAsString() {
			if (getFlexoConcept() == null) {
				return null;
			}
			if (identifierAsString == null) {
				identifierAsString = getVirtualModelInstance().getIdentifierAsString(hbnMap, getFlexoConcept());
			}
			return identifierAsString;
		}

		@Override
		public HbnObjectActorReference makeActorReference(FlexoConceptInstanceRole role, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory<?> factory = getFactory();
			HbnObjectActorReference returned = factory.newInstance(HbnObjectActorReference.class);
			returned.setFlexoRole(role);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(this);
			return returned;
		}

	}
}
