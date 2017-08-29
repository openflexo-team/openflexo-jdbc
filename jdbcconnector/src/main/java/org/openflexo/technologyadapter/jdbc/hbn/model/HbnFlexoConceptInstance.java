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

import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;

/**
 * A JDBC/Hibernate-specific {@link FlexoConceptInstance} reflecting a distant object accessible in an {@link HbnVirtualModelInstance}
 * through a {@link HbnModelSlot}
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(HbnFlexoConceptInstance.HbnFlexoConceptInstanceImpl.class)
public interface HbnFlexoConceptInstance extends FlexoConceptInstance {

	public String getIdentifier();

	abstract class HbnFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements HbnFlexoConceptInstance {

		@Override
		public HbnVirtualModelInstance getVirtualModelInstance() {
			return (HbnVirtualModelInstance) super.getVirtualModelInstance();
		}

		@Override
		public <T> T getFlexoPropertyValue(FlexoProperty<T> flexoProperty) {
			/*if (getFlexoConcept().getDeclaredProperties().contains(flexoProperty) && flexoProperty instanceof AbstractProperty
					&& getSupport() != null) {
				// System.out.println("support = " + support);
				return (T) getSupport().getValue(flexoProperty.getName(), flexoProperty.getType());
			}*/
			return super.getFlexoPropertyValue(flexoProperty);
		}

		@Override
		public <T> void setFlexoPropertyValue(FlexoProperty<T> flexoProperty, T value) {
			/*if (flexoProperty instanceof AbstractProperty && getSupport() != null) {
				T oldValue = getFlexoPropertyValue(flexoProperty);
				if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue))) {
					getSupport().setValue(flexoProperty.getName(), value);
					setIsModified();
					getPropertyChangeSupport().firePropertyChange(flexoProperty.getPropertyName(), oldValue, value);
				}
			}
			else {*/
			super.setFlexoPropertyValue(flexoProperty, value);
			// }
		}

		/*@Override
		public String getReferenceForSerialization(boolean serializeClassName) {
			AccessPoint accessPoint = getVirtualModelInstance().getAccessPoint();
			String resourceURI = accessPoint.getResource().getURI();
			String conceptName = getFlexoConcept().getName();
			return FlexoObjectReference.constructSerializationRepresentation(null, resourceURI, getUserIdentifier(),
					support.getIdentifier(), conceptName);
		}*/

		@Override
		public String getIdentifier() {
			// TODO: implement caching
			if (getFlexoConcept() == null) {
				return null;
			}
			if (identifier == null) {
				if (getFlexoConcept() != null) {
					if (getFlexoConcept().getKeyProperties().size() > 1) {
						StringBuffer sb = new StringBuffer();
						boolean isFirst = true;
						for (FlexoProperty<?> keyP : getFlexoConcept().getKeyProperties()) {
							sb.append((isFirst ? "" : ",") + keyP.getName() + "=" + getFlexoPropertyValue(keyP));
							isFirst = false;
						}
						identifier = sb.toString();
					}
					else if (getFlexoConcept().getKeyProperties().size() > 0) {
						Object keyValue = getFlexoPropertyValue(getFlexoConcept().getKeyProperties().get(0));
						if (keyValue != null) {
							identifier = keyValue.toString();
						}
					}
				}
			}
			return identifier;
		}

		private String identifier = null;

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
