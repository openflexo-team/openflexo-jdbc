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
import java.util.Map;

import org.openflexo.foundation.fml.AbstractProperty;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnReferenceRole;

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
	 * Default implementation for {@link HbnFlexoConceptInstance}
	 * 
	 * @author sylvain
	 *
	 */
	abstract class HbnFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements HbnFlexoConceptInstance {

		// Hibernate support object
		private Map<String, Object> hbnMap;

		private Serializable identifier = null;
		private String identifierAsString = null;

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

		private HbnFlexoConceptInstance getReferencedObject(HbnReferenceRole referenceRole) {
			System.out.println("Tiens c'est qui le " + referenceRole);
			Map<String, Object> refHbnMap = (Map<String, Object>) hbnMap.get(referenceRole.getName());
			if (refHbnMap != null) {
				System.out.println("Ce serait pas: " + refHbnMap);
				return getVirtualModelInstance().getFlexoConceptInstance(refHbnMap, null, referenceRole.getFlexoConceptType());
			}
			return null;
		}

		@Override
		public <T> T getFlexoActor(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof HbnReferenceRole) {
				return (T) getReferencedObject((HbnReferenceRole) flexoRole);
			}
			return super.getFlexoActor(flexoRole);
		}

		@Override
		public <T> T getFlexoPropertyValue(FlexoProperty<T> flexoProperty) {
			if (flexoProperty instanceof AbstractProperty) {
				T returned = (T) hbnMap.get(flexoProperty.getName());
				return returned;
			}

			if (flexoProperty instanceof HbnReferenceRole) {
				return (T) getReferencedObject((HbnReferenceRole) flexoProperty);
			}

			return super.getFlexoPropertyValue(flexoProperty);
		}

		@Override
		public <T> void setFlexoPropertyValue(FlexoProperty<T> flexoProperty, T value) {
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
			returned.setFlexoRole((FlexoRole) role);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(this);
			return returned;
		}

	}
}
