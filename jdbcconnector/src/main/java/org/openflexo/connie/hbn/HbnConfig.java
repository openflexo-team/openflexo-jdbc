/**
 * 
 * Copyright (c) 2017, Openflexo
 * This file is part of Connie-core, a component of the software infrastructure 
 * developed at Openflexo.
 * 
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
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
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

package org.openflexo.connie.hbn;

import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.openflexo.connie.BindingVariable;

public class HbnConfig {

	private static final Logger LOGGER = Logger.getLogger(BindingVariable.class.getPackage().getName());

	// bootstrapping
	private final BootstrapServiceRegistry hbnBsRegistry;
	private final MetadataSources metadataSrc;
	private Properties properties;
	// private ClassLoaderAccessImpl classLoaderAccess;

	private MetadataBuilder metadataBuilder;

	private StandardServiceRegistryBuilder hbnRegistryBuilder;
	private StandardServiceRegistry serviceRegistry;

	private Metadata metadata;

	private SessionFactory sessionFactory;

	/**
	 * Default Constructor
	 * 
	 * @param serviceRegistry
	 */
	public HbnConfig(BootstrapServiceRegistry serviceRegistry) {
		hbnBsRegistry = serviceRegistry;
		metadataSrc = new MetadataSources(serviceRegistry);
		properties = new Properties();

		hbnRegistryBuilder = new StandardServiceRegistryBuilder(hbnBsRegistry);
	}

	// ****************************
	// hibernate configuration / properties

	public String getProperty(String propertyName) {
		Object o = properties.get(propertyName);
		return o instanceof String ? (String) o : null;
	}

	public HbnConfig setProperty(String propertyName, String value) {
		if (value != null) {
			properties.setProperty(propertyName, value);
		}
		return this;
	}

	// ****************************
	// Metadata sources

	public HbnConfig addFile(String xmlFile) throws MappingException {
		metadataSrc.addFile(xmlFile);
		return this;
	}

	public HbnConfig addResource(String resource) throws MappingException {
		metadataSrc.addResource(resource);
		return this;
	}

	public HbnConfig addAnnotatedClass(Class<?> annotatedClass) {
		metadataSrc.addAnnotatedClass(annotatedClass);
		return this;
	}

	// ****************************
	// Service Registry building

	public StandardServiceRegistry getServiceRegistry() {

		if (serviceRegistry == null) {
			hbnRegistryBuilder.applySettings(properties);
			serviceRegistry = hbnRegistryBuilder.build();
		}
		else {
			LOGGER.warning("ServiceRegistry has already been configured");
		}

		return serviceRegistry;
	}

	// ****************************
	// Metadata building

	public MetadataBuilder getBuilder() {

		// TODO: manage change in properties => needs to change builderConfig

		LOGGER.fine("Build Metadata");

		if (metadataBuilder == null) {

			serviceRegistry = getServiceRegistry();

			metadataBuilder = metadataSrc.getMetadataBuilder(serviceRegistry);
		}
		else {
			LOGGER.warning("MetadataBuilder has already been configured");
		}

		return metadataBuilder;

	}

	// ****************************
	// Metadata building

	public Metadata getMetadata() {

		if (metadata == null) {

			MetadataBuilder builder = getBuilder();

			metadata = builder.build();
		}
		else {
			LOGGER.warning("Metadata has already been generated");
		}

		return metadata;
	}

	// ****************************
	// SessionFactory building

	public SessionFactory getSessionFactory() {

		if (sessionFactory == null) {
			Metadata metadata = getMetadata();

			final SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();

			sessionFactory = sessionFactoryBuilder.build();
		}
		else {
			LOGGER.warning("Session Factory has already been built");
		}

		return sessionFactory;
	}

	// ****************************
	// EntityManager building

	public EntityManager createEntityManager() {
		return getSessionFactory().createEntityManager();
	}

	// ****************************
	// Utils

	public JdbcEnvironment getJdbcEnv() {
		if (serviceRegistry != null) {
			return serviceRegistry.getService(JdbcEnvironment.class);
		}
		else {
			return null;
		}
	}

}
