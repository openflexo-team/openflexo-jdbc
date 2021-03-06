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

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.query.Query;
import org.hibernate.type.TypeResolver;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.AbstractProperty;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.jdbc.HbnConfig;
import org.openflexo.technologyadapter.jdbc.HbnModelSlot;
import org.openflexo.technologyadapter.jdbc.JDBCTechnologyAdapter;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnColumnRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnOneToManyReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.fml.HbnToOneReferenceRole;
import org.openflexo.technologyadapter.jdbc.hbn.model.HbnVirtualModelInstance.HbnVirtualModelInstanceImpl;
import org.openflexo.technologyadapter.jdbc.model.JDBCColumn;
import org.openflexo.technologyadapter.jdbc.model.JDBCConnection;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.technologyadapter.jdbc.rm.JDBCResource;
import org.openflexo.toolbox.StringUtils;

/**
 * A JDBC/Hibernate-specific {@link VirtualModelInstance} reflecting distants objects accessible through a {@link HbnModelSlot} configured
 * with a {@link VirtualModel}<br>
 * 
 * This {@link VirtualModelInstance} implementation implements Hibernate framework on a given database to provide a database connection.<br>
 * Call {@link #connectToDB()} to realize connection to database. This call initialize a {@link SessionFactory} and open a defaut
 * connection.
 * 
 * This {@link HbnVirtualModelInstance} has an internal caching scheme allowing to store {@link HbnFlexoConceptInstance} relatively to their
 * related {@link FlexoConcept} (their type) and their identifier
 * 
 * 
 */
@ModelEntity
@ImplementationClass(HbnVirtualModelInstanceImpl.class)
@Imports(@Import(HbnFlexoConceptInstance.class))
@XMLElement
public interface HbnVirtualModelInstance extends VirtualModelInstance<HbnVirtualModelInstance, JDBCTechnologyAdapter> {

	/*@PropertyIdentifier(type = JDBCDbType.class)
	String DB_TYPE = "dbtype";
	String ADDRESS_KEY = "address";
	String USER_KEY = "user";
	String PASSWORD_KEY = "password";*/

	/*@Getter(DB_TYPE)
	@XMLAttribute
	JDBCDbType getDbType();
	
	@Setter(DB_TYPE)
	void setDbType(JDBCDbType aType);
	
	@Getter(ADDRESS_KEY)
	@XMLAttribute
	String getAddress();
	
	@Setter(ADDRESS_KEY)
	void setAddress(String address);
	
	@Getter(USER_KEY)
	@XMLAttribute
	String getUser();
	
	@Setter(USER_KEY)
	void setUser(String user);
	
	@Getter(PASSWORD_KEY)
	@XMLAttribute
	String getPassword();
	
	@Setter(PASSWORD_KEY)
	void setPassword(String password);*/

	@PropertyIdentifier(type = JDBCResource.class)
	String JDBC_CONNECTION_RESOURCE = "JDBCConnectionResource";
	@PropertyIdentifier(type = String.class)
	String JDBC_CONNECTION_URI = "JDBCConnectionURI";

	@Getter(JDBC_CONNECTION_RESOURCE)
	public JDBCResource getJDBCConnectionResource();

	@Setter(JDBC_CONNECTION_RESOURCE)
	public void setJDBCConnectionResource(JDBCResource jdbcConnectionResource);

	@Getter(JDBC_CONNECTION_URI)
	@XMLAttribute
	public String getJDBCConnectionURI();

	@Setter(JDBC_CONNECTION_URI)
	public void setJDBCConnectionURI(String jdbcConnectionURI);

	/**
	 * Return {@link JDBCConnection}<br>
	 * Return null when not fully configured (address, user and password)
	 * 
	 */
	public JDBCConnection getJDBCConnection();

	/**
	 * Realize the connection to the database<br>
	 * 
	 * This should be called only when the {@link HbnVirtualModelInstance} is fully configured (address, user, password and contract
	 * {@link VirtualModel})<br>
	 * 
	 * First open JDBC connection, then initialize database definition, declare mapping, initialize {@link SessionFactory} and open a
	 * default {@link Session}<br>
	 * 
	 * @throws HbnException
	 *             when something wrong happen
	 * 
	 */
	public void connectToDB() throws FlexoException;

	/**
	 * Return boolean indicating if this {@link HbnVirtualModelInstance} is connected to database
	 * 
	 * @return
	 */
	public boolean isConnected();

	/**
	 * Connect to database when not connected yet, and returned default shared session (shoud not be used in production with concurrent
	 * accesses)
	 * 
	 * @return
	 * @throws HbnException
	 */
	public Session getDefaultSession() throws HbnException;

	/**
	 * Connect to database when not connected yet, and open a new session
	 * 
	 * @return
	 * @throws HbnException
	 */
	public Session openSession() throws HbnException;

	/**
	 * Close supplied session
	 * 
	 * @param session
	 */
	public void closeSession(Session session);

	/**
	 * Begin an Hibernate transaction
	 * 
	 * If a current transaction is active, commit it first.
	 * 
	 * @return
	 */
	public Transaction beginTransaction() throws HbnException;

	/**
	 * Commit current active transaction
	 */
	public void commit() throws HbnException;

	/**
	 * Rollback current active transaction
	 */
	public void rollback() throws HbnException;

	/**
	 * Build and return a {@link Serializable} object which acts as key identifier for supplied hbnMap, asserting this is the support object
	 * for a {@link HbnFlexoConceptInstance} of supplied {@link FlexoConcept}
	 * 
	 * <ul>
	 * <li>If key of declaring {@link FlexoConcept} is simple, just return the value of key property</li>
	 * <li>If Key is composite, return an Object array with all the values of properties composing composite key, in the order where those
	 * properties are declared in keyProperties of related {@link FlexoConcept}</li>
	 * </ul>
	 * 
	 * @param hbnMap
	 *            hibernate support object
	 * @param concept
	 *            type of related {@link HbnFlexoConceptInstance}
	 * @return
	 */
	public Serializable getIdentifier(Map<String, Object> hbnMap, FlexoConcept concept);

	/**
	 * Build and return a {@link String} object which acts as string representation of an identifier for supplied hbnMap, asserting this is
	 * the support object for a {@link HbnFlexoConceptInstance} of supplied {@link FlexoConcept}
	 * 
	 * <ul>
	 * <li>If key of declaring {@link FlexoConcept} is simple, just return the String value of key property</li>
	 * <li>If Key is composite, return an comma-separated dictionary as a String with all the values of properties composing composite key,
	 * in the order where those properties are declared in keyProperties of related {@link FlexoConcept}</li>
	 * </ul>
	 * 
	 * @param hbnMap
	 *            hibernate support object
	 * @param concept
	 *            type of related {@link HbnFlexoConceptInstance}
	 * @return
	 */
	public String getIdentifierAsString(Map<String, Object> hbnMap, FlexoConcept concept);

	/**
	 * Retrieve (build when non-existant) a list of {@link HbnFlexoConceptInstance} matching supplied {@link Query} asserting returned
	 * {@link HbnFlexoConceptInstance} objects have supplied concept type and container<br>
	 *
	 * @param query
	 *            query whose results have to be wrapped into {@link HbnFlexoConceptInstance} list
	 * @param container
	 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
	 * @param concept
	 *            type of returned {@link HbnFlexoConceptInstance}
	 * @return
	 */
	public List<HbnFlexoConceptInstance> getFlexoConceptInstances(Query<?> query, FlexoConceptInstance container, FlexoConcept concept);

	/**
	 * Retrieve (build if not existant) {@link HbnFlexoConceptInstance} with supplied support object (a map managed by Hibernate Framework),
	 * asserting returned {@link HbnFlexoConceptInstance} object has supplied concept type and container<br>
	 * 
	 * This {@link HbnVirtualModelInstance} has an internal caching scheme allowing to store {@link HbnFlexoConceptInstance} relatively to
	 * their related {@link FlexoConcept} (their type) and their identifier
	 * 
	 * @param hbnMap
	 *            hibernate support object
	 * @param container
	 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
	 * @param concept
	 *            type of returned {@link HbnFlexoConceptInstance}
	 * @return
	 */
	public HbnFlexoConceptInstance getFlexoConceptInstance(Map<String, Object> hbnMap, FlexoConceptInstance container,
			FlexoConcept concept);

	/**
	 * Retrieve (build if not existant) {@link HbnFlexoConceptInstance} with supplied identifier asserting returned
	 * {@link HbnFlexoConceptInstance} object has supplied concept type and container<br>
	 * 
	 * This {@link HbnVirtualModelInstance} has an internal caching scheme allowing to store {@link HbnFlexoConceptInstance} relatively to
	 * their related {@link FlexoConcept} (their type) and their identifier
	 * 
	 * @param identifier
	 *            identifier for searched object
	 * @param container
	 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
	 * @param concept
	 *            type of returned {@link HbnFlexoConceptInstance}
	 * @return
	 */
	public HbnFlexoConceptInstance getFlexoConceptInstance(Serializable identifier, FlexoConceptInstance container, FlexoConcept concept);

	/**
	 * Instantiate and register a new {@link HbnFlexoConceptInstance}
	 * 
	 * @param pattern
	 * @return
	 */
	@Override
	public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept);

	/**
	 * Instantiate and register a new {@link HbnFlexoConceptInstance} in a container FlexoConceptInstance
	 * 
	 * @param pattern
	 * @return
	 */
	@Override
	public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, FlexoConceptInstance container);

	public Map<FlexoConcept, PersistentClass> getMappings();

	abstract class HbnVirtualModelInstanceImpl extends VirtualModelInstanceImpl<HbnVirtualModelInstance, JDBCTechnologyAdapter>
			implements HbnVirtualModelInstance {

		private static final Logger logger = FlexoLogger.getLogger(HbnVirtualModelInstance.class.getPackage().toString());

		// private JDBCConnection jdbcConnection;

		protected HbnConfig config;
		protected MetadataBuildingOptionsImpl buildingOptions;
		protected InFlightMetadataCollectorImpl metadataCollector;
		protected Metadata metadata;
		protected SessionFactory sessionFactory;
		protected Session defaultSession;

		private boolean isConnected = false;

		// Stores all FCIs related to their identifier
		private Map<FlexoConcept, Map<Object, HbnFlexoConceptInstance>> instances = new HashMap<>();

		private JDBCResource jdbcConnectionResource;
		private String jdbcConnectionURI;

		@Override
		public JDBCResource getJDBCConnectionResource() {
			if (jdbcConnectionResource == null && StringUtils.isNotEmpty(jdbcConnectionURI) && getServiceManager() != null
					&& getServiceManager().getResourceManager() != null) {
				jdbcConnectionResource = (JDBCResource) getServiceManager().getResourceManager().getResource(jdbcConnectionURI,
						JDBCConnection.class);
				logger.info("Looked-up " + jdbcConnectionResource + " for " + jdbcConnectionURI);
			}
			return jdbcConnectionResource;
		}

		@Override
		public void setJDBCConnectionResource(JDBCResource jdbcConnectionResource) {
			this.jdbcConnectionResource = jdbcConnectionResource;
		}

		@Override
		public String getJDBCConnectionURI() {
			if (jdbcConnectionResource != null) {
				return jdbcConnectionResource.getURI();
			}
			return jdbcConnectionURI;
		}

		@Override
		public void setJDBCConnectionURI(String jdbcConnectionURI) {
			this.jdbcConnectionURI = jdbcConnectionURI;
		}

		@Override
		public JDBCTechnologyAdapter getTechnologyAdapter() {
			if (getResource() != null) {
				return getResource().getTechnologyAdapter();
			}
			return null;
		}

		/**
		 * Return boolean indicating if this {@link HbnVirtualModelInstance} is connected to database
		 * 
		 * @return
		 */
		@Override
		public boolean isConnected() {
			return isConnected;
		}

		/**
		 * Realize the connection to the database<br>
		 * 
		 * This should be called only when the {@link HbnVirtualModelInstance} is fully configured (address, user, password and contract
		 * {@link VirtualModel})<br>
		 * 
		 * First open JDBC connection, then initialize database definition, declare mapping, initialize {@link SessionFactory} and open a
		 * default {@link Session}<br>
		 * 
		 * @throws HbnException
		 *             when something wrong happen
		 * 
		 */
		@Override
		public void connectToDB() throws HbnException {

			if (isConnected) {
				return;
			}

			if (getVirtualModel() == null) {
				throw new HbnException("VirtualModel not defined");
			}

			// open connection
			if (getJDBCConnection().getConnection() == null) {
				throw new HbnException("Could not connect to database " + getJDBCConnection().getAddress());
			}

			// create config
			createHbnConfig();

			// set database definition
			initializeDatabaseDefinition();

			// declare Hibernate mapping
			declareHbnMapping();

			// create session factory
			sessionFactory = metadata.buildSessionFactory();

			defaultSession = sessionFactory.withOptions().openSession();

			isConnected = true;
		}

		/**
		 * Return {@link JDBCConnection}<br>
		 * 
		 */
		@Override
		public JDBCConnection getJDBCConnection() {
			if (getJDBCConnectionResource() != null) {
				try {
					return getJDBCConnectionResource().getResourceData();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					e.printStackTrace();
				} catch (FlexoException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * Connect to database when not connected yet, and returned default shared session (shoud not be used in production with concurrent
		 * accesses)
		 * 
		 * @return
		 * @throws HbnException
		 */
		@Override
		public Session getDefaultSession() throws HbnException {
			if (!isConnected) {
				connectToDB();
			}
			return defaultSession;
		}

		/**
		 * Connect to database when not connected yet, and open a new session
		 * 
		 * @return
		 * @throws HbnException
		 */
		@Override
		public Session openSession() throws HbnException {
			if (!isConnected) {
				connectToDB();
			}
			return sessionFactory.withOptions().openSession();
		}

		/**
		 * Close supplied session
		 * 
		 * @param session
		 */
		@Override
		public void closeSession(Session session) {
			session.close();
		}

		/**
		 * Internally called to create Hibernate configuration
		 */
		private void createHbnConfig() {

			config = new HbnConfig(new BootstrapServiceRegistryBuilder().build());

			System.out.println("Bon on est la avec " + jdbcConnectionResource);
			System.out.println("Bon on est la avec " + getJDBCConnection());
			System.out.println("Bon on est la avec " + getJDBCConnection().getDbType());

			config.setProperty("hibernate.connection.driver_class", getJDBCConnection().getDbType().getDriverClassName());
			config.setProperty("hibernate.connection.url", getJDBCConnection().getAddress());
			config.setProperty("hibernate.connection.username", getJDBCConnection().getUser());
			config.setProperty("hibernate.connection.password", getJDBCConnection().getPassword());
			config.setProperty("hibernate.connection.pool_size", "1");
			config.setProperty("hibernate.dialect", getJDBCConnection().getDbType().getHibernateDialect());
			config.setProperty("hibernate.show_sql", "true");
			// creates object, wipe out if already exists
			// config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

			buildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(config.getServiceRegistry());
			metadataCollector = new InFlightMetadataCollectorImpl(buildingOptions, new TypeResolver());
		}

		/**
		 * Internally called to initialize database definition (should reflect in Hibernate API the structure of accessed database)
		 */
		private void initializeDatabaseDefinition() {

			System.out.println("initializeDatabaseDefinition()");

			for (JDBCTable table : getJDBCConnection().getSchema().getTables()) {
				initTableDefinition(table);
			}
		}

		/**
		 * Internally called to initialize database definition for a {@link JDBCTable} (should reflect in Hibernate API the structure of
		 * accessed table)
		 * 
		 * @param jdbcTable
		 */
		private void initTableDefinition(JDBCTable jdbcTable) {

			Table table = metadataCollector.addTable("", "", jdbcTable.getName(), null, false);
			table.setName(jdbcTable.getName());

			for (JDBCColumn jdbcColumn : jdbcTable.getColumns()) {
				Column col = new Column();
				col.setName(jdbcColumn.getName());
				// System.out.println("Define new column " + jdbcColumn.getName() + " type=" + jdbcColumn.getDataType());
				col.setLength(jdbcColumn.getLength());
				col.setSqlType(jdbcColumn.getSQLType());
				col.setNullable(jdbcColumn.isNullable());
				table.addColumn(col);
				if (jdbcColumn.isPrimaryKey()) {
					PrimaryKey pk = new PrimaryKey(table);
					pk.addColumn(col);
				}
			}

		}

		/**
		 * Internally called to declare Hibernate mapping between internal database structure and reflected {@link FlexoConcept} instances
		 * 
		 * @throws HbnException
		 */
		private void declareHbnMapping() throws HbnException {

			System.out.println("declareHbnMapping()");

			if (getVirtualModel() == null) {
				throw new HbnException("VirtualModel not defined");
			}

			MetadataBuildingContextRootImpl metadataBuildingContext = new MetadataBuildingContextRootImpl(buildingOptions,
					new ClassLoaderAccessImpl(null, config.getServiceRegistry()), metadataCollector);
			metadata = metadataCollector.buildMetadataInstance(metadataBuildingContext);

			Namespace namespace = metadataCollector.getDatabase().getDefaultNamespace();

			Map<FlexoConcept, RootClass> hbnMappings = new HashMap<>();
			Map<FlexoConcept, Table> tables = new HashMap<>();

			// Mapping is created in 3 passes, in order to facilitate cross references handling

			for (FlexoConcept concept : getVirtualModel().getFlexoConcepts()) {
				Identifier logicalName = metadataCollector.getDatabase().toIdentifier(concept.getName());
				Table table = namespace.locateTable(logicalName);
				if (table == null) {
					throw new HbnException("Could not locate table " + concept.getName());
				}
				RootClass newMapping = declareHbnMapping(concept, table, metadataBuildingContext);
				hbnMappings.put(concept, newMapping);
				tables.put(concept, table);
			}

			for (FlexoConcept concept : hbnMappings.keySet()) {
				Table table = tables.get(concept);
				configureHbnMapping(hbnMappings.get(concept), concept, table, metadataBuildingContext);
			}

			for (FlexoConcept concept : hbnMappings.keySet()) {
				Table table = tables.get(concept);
				finalizeHbnMapping(hbnMappings.get(concept), concept, table, metadataBuildingContext);
			}

			try {
				((MetadataImplementor) metadata).validate();
			} catch (MappingException e) {
				logger.warning("Validation Error: " + e.getMessage());
				e.printStackTrace();
				throw new HbnException(e);
			} catch (Exception e) {
				logger.warning("Unexpected exception thrown during validation: " + e.getMessage());
				throw new HbnException(e);
			}
		}

		private Map<FlexoConcept, PersistentClass> mappings = new HashMap<>();

		@Override
		public Map<FlexoConcept, PersistentClass> getMappings() {
			return mappings;
		}

		/**
		 * Internally called to declare mapping for a given {@link FlexoConcept}<br>
		 * 
		 * This is the first pass
		 * 
		 * @param concept
		 * @param table
		 * @param metadataBuildingContext
		 */
		private RootClass declareHbnMapping(FlexoConcept concept, Table table, MetadataBuildingContext metadataBuildingContext) {

			// System.out.println("1st pass : Declare mapping for concept " + concept.getName() + " table=" + table);

			RootClass pClass = new RootClass(metadataBuildingContext);
			mappings.put(concept, pClass);
			pClass.setEntityName(concept.getName());
			pClass.setJpaEntityName(concept.getName());
			pClass.setTable(table);
			metadataCollector.addEntityBinding(pClass);

			return pClass;

		}

		/**
		 * Internally called to configure mapping for a given {@link FlexoConcept}
		 * 
		 * This is the second pass
		 * 
		 * @param concept
		 * @param table
		 * @param metadataBuildingContext
		 */
		private PersistentClass configureHbnMapping(RootClass pClass, FlexoConcept concept, Table table,
				MetadataBuildingContext metadataBuildingContext) {

			// System.out.println("2nd pass : Configure mapping for concept " + concept.getName() + " table=" + table);

			for (FlexoProperty<?> flexoProperty : concept.getDeclaredProperties()) {
				Property prop;

				if (flexoProperty instanceof HbnColumnRole) {

					HbnColumnRole<?> hbnColumnRole = (HbnColumnRole<?>) flexoProperty;

					Identifier colIdentifier = metadataCollector.getDatabase().toIdentifier(hbnColumnRole.getColumnName());
					// System.out.println("colIdentifier: " + colIdentifier);
					Column col = table.getColumn(colIdentifier);
					// System.out.println("col: " + col);

					prop = new Property();
					prop.setName(hbnColumnRole.getName());
					SimpleValue value = new SimpleValue((MetadataImplementor) metadata, table);
					value.setTypeName(TypeUtils.getBaseClass(hbnColumnRole.getType()).getCanonicalName());
					value.addColumn(col);
					value.setTable(table);
					prop.setValue(value);
					if (concept.getKeyProperties().contains(flexoProperty)) {
						// This is a key !
						if (hbnColumnRole.getType().equals(Integer.class)) {
							/*value.setIdentifierGeneratorStrategy("native");
							Properties params = new Properties();
							params.setProperty(PersistentIdentifierGenerator.PK, flexoProperty.getName());
							value.setIdentifierGeneratorProperties(params);*/
							value.setIdentifierGeneratorStrategy("native");
						}
						else {
							value.setIdentifierGeneratorStrategy("assigned");
						}
						pClass.setDeclaredIdentifierProperty(prop);
						pClass.setIdentifierProperty(prop);
						pClass.setIdentifier(value);
					}
					else {
						pClass.addProperty(prop);
					}
				}

				// Following is deprecated
				if (flexoProperty instanceof AbstractProperty) {
					AbstractProperty<?> abstractProperty = (AbstractProperty<?>) flexoProperty;

					Identifier colIdentifier = metadataCollector.getDatabase().toIdentifier(abstractProperty.getName());
					// System.out.println("colIdentifier: " + colIdentifier);
					Column col = table.getColumn(colIdentifier);
					// System.out.println("col: " + col);

					prop = new Property();
					prop.setName(abstractProperty.getName());
					SimpleValue value = new SimpleValue((MetadataImplementor) metadata, table);
					value.setTypeName(TypeUtils.getBaseClass(abstractProperty.getType()).getCanonicalName());
					value.addColumn(col);
					value.setTable(table);
					prop.setValue(value);
					if (concept.getKeyProperties().contains(flexoProperty)) {
						// This is a key !
						if (abstractProperty.getType().equals(Integer.class)) {
							value.setIdentifierGeneratorStrategy("native");
						}
						else {
							value.setIdentifierGeneratorStrategy("assigned");
						}
						pClass.setDeclaredIdentifierProperty(prop);
						pClass.setIdentifierProperty(prop);
						pClass.setIdentifier(value);
					}
					else {
						pClass.addProperty(prop);
					}
				}
				// End deprecated

				else if (flexoProperty instanceof HbnToOneReferenceRole) {
					HbnToOneReferenceRole referenceRole = (HbnToOneReferenceRole) flexoProperty;

					// TODO: define a column name
					Identifier colIdentifier = metadataCollector.getDatabase().toIdentifier(referenceRole.getColumnName());
					// System.out.println("colIdentifier: " + colIdentifier);
					Column col = table.getColumn(colIdentifier);
					// System.out.println("col: " + col);

					prop = new Property();
					prop.setName(referenceRole.getName());
					ManyToOne manyToOne = new ManyToOne((MetadataImplementor) metadata, table);
					manyToOne.setReferencedPropertyName(referenceRole.getForeignKeyAttributeName());
					// When the Concept is not part of the mapping, cannot reference FlexoConceptType as it does not exist in VM
					if (referenceRole.getFlexoConceptType() != null) {
						manyToOne.setReferencedEntityName(referenceRole.getFlexoConceptType().getName());
					}
					manyToOne.addColumn(col);
					manyToOne.setTable(table);
					prop.setValue(manyToOne);
					pClass.addProperty(prop);
				}

			}

			return pClass;

		}

		/**
		 * Internally called to configure mapping for a given {@link FlexoConcept}
		 * 
		 * This is the third and last pass
		 * 
		 * @param concept
		 * @param table
		 * @param metadataBuildingContext
		 */
		private PersistentClass finalizeHbnMapping(RootClass pClass, FlexoConcept concept, Table table,
				MetadataBuildingContext metadataBuildingContext) {

			// System.out.println("3rd pass: Configure mapping for concept " + concept.getName() + " table=" + table);

			for (FlexoProperty<?> flexoProperty : concept.getDeclaredProperties()) {

				if (flexoProperty instanceof HbnOneToManyReferenceRole) {
					HbnOneToManyReferenceRole referenceRole = (HbnOneToManyReferenceRole) flexoProperty;

					if (referenceRole.getFlexoConceptType() == null) {
						logger.warning("Undefined reference concept for " + referenceRole);
						break;
					}

					PersistentClass oppositeClass = mappings.get(referenceRole.getFlexoConceptType());
					Table oppositeTable = oppositeClass.getTable();
					Identifier colIdentifier = metadataCollector.getDatabase().toIdentifier(referenceRole.getDestinationKeyColumnName());
					Column col = oppositeTable.getColumn(colIdentifier);

					Bag coll = new Bag((MetadataImplementor) metadata, pClass);
					coll.setRole(pClass.getEntityName() + "." + referenceRole.getPropertyName());
					coll.setCollectionTable(oppositeTable);

					OneToMany oneToMany = new OneToMany((MetadataImplementor) metadata, pClass);
					coll.setElement(oneToMany);
					oneToMany.setReferencedEntityName(referenceRole.getFlexoConceptType().getName());
					oneToMany.setAssociatedClass(oppositeClass);
					// coll.setReferencedPropertyName("ID");

					DependantValue dv = new DependantValue((MetadataImplementor) metadata, oppositeTable, oppositeClass.getKey());
					dv.addColumn(col);

					List<Column> manyColumns = new ArrayList<>(); // Relationship columns in tableMany
					manyColumns.add(col);
					// oppositeTable.createForeignKey(referenceRole.getDestinationKeyAttributeName(), manyColumns,
					// pClass.getEntityName(),"prout");
					dv.setNullable(false);
					coll.setKey(dv);

					metadataCollector.addCollectionBinding(coll);

					Property prop = new Property();
					prop.setName(referenceRole.getName());
					prop.setValue(coll);
					pClass.addProperty(prop);

				}
			}

			return pClass;

		}

		@SuppressWarnings("unchecked")
		@Override
		public List<FlexoConceptInstance> getFlexoConceptInstances() {
			if (isSerializing()) {
				// FCI are not serialized
				return null;
			}
			return (List<FlexoConceptInstance>) performSuperGetter(FLEXO_CONCEPT_INSTANCES_KEY);
		}

		/**
		 * Build and return a {@link Serializable} object which acts as key identifier for supplied hbnMap, asserting this is the support
		 * object for a {@link HbnFlexoConceptInstance} of supplied {@link FlexoConcept}
		 * 
		 * <ul>
		 * <li>If key of declaring {@link FlexoConcept} is simple, just return the value of key property</li>
		 * <li>If Key is composite, return an Object array with all the values of properties composing composite key, in the order where
		 * those properties are declared in keyProperties of related {@link FlexoConcept}</li>
		 * </ul>
		 * 
		 * @param hbnMap
		 * @param concept
		 * @return
		 */
		@Override
		public Serializable getIdentifier(Map<String, Object> hbnMap, FlexoConcept concept) {
			if (concept.getKeyProperties().size() == 0) {
				return null;
			}
			if (concept.getKeyProperties().size() == 1) {
				return (Serializable) hbnMap.get(concept.getKeyProperties().get(0).getName());
			}
			// composite key
			Object[] returned = new Object[concept.getKeyProperties().size()];
			for (int i = 0; i < concept.getKeyProperties().size(); i++) {
				returned[i] = hbnMap.get(concept.getKeyProperties().get(i).getName());
			}
			return returned;
		}

		/**
		 * Build and return a {@link String} object which acts as string representation of an identifier for supplied hbnMap, asserting this
		 * is the support object for a {@link HbnFlexoConceptInstance} of supplied {@link FlexoConcept}
		 * 
		 * <ul>
		 * <li>If key of declaring {@link FlexoConcept} is simple, just return the String value of key property</li>
		 * <li>If Key is composite, return an comma-separated dictionary as a String with all the values of properties composing composite
		 * key, in the order where those properties are declared in keyProperties of related {@link FlexoConcept}</li>
		 * </ul>
		 * 
		 * @param hbnMap
		 * @param concept
		 * @return
		 */
		@Override
		public String getIdentifierAsString(Map<String, Object> hbnMap, FlexoConcept concept) {
			if (concept.getKeyProperties().size() == 0) {
				return null;
			}
			if (concept.getKeyProperties().size() == 1) {
				return hbnMap.get(concept.getKeyProperties().get(0).getName()).toString();
			}
			// composite key
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			for (FlexoProperty<?> keyP : concept.getKeyProperties()) {
				sb.append((isFirst ? "" : ",") + keyP.getName() + "=" + hbnMap.get(keyP.getName()));
				isFirst = false;
			}
			return sb.toString();
		}

		@Override
		public HbnVirtualModelInstanceModelFactory getFactory() {
			return (HbnVirtualModelInstanceModelFactory) super.getFactory();
		}

		/**
		 * Retrieve (build if not existant) {@link HbnFlexoConceptInstance} with supplied support object (a map managed by Hibernate
		 * Framework), asserting returned {@link HbnFlexoConceptInstance} object has supplied concept type and container<br>
		 * 
		 * This {@link HbnVirtualModelInstance} has an internal caching scheme allowing to store {@link HbnFlexoConceptInstance} relatively
		 * to their related {@link FlexoConcept} (their type) and their identifier
		 * 
		 * @param hbnMap
		 *            hibernate support object
		 * @param container
		 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
		 * @param concept
		 *            type of returned {@link HbnFlexoConceptInstance}
		 * @return
		 */
		@Override
		public HbnFlexoConceptInstance getFlexoConceptInstance(Map<String, Object> hbnMap, FlexoConceptInstance container,
				FlexoConcept concept) {

			if (concept == null) {
				logger.warning("Could not obtain HbnFlexoConceptInstance with null FlexoConcept");
			}

			Object identifier = getIdentifier(hbnMap, concept);
			// System.out.println("Building object with: " + hbnMap + " id=" + identifier);

			Map<Object, HbnFlexoConceptInstance> mapForConcept = instances.computeIfAbsent(concept, (newConcept) -> {
				return new HashMap<>();
			});

			return mapForConcept.computeIfAbsent(identifier, (newId) -> {
				return getFactory().newFlexoConceptInstance(this, container, hbnMap, concept);
			});
		}

		/**
		 * Retrieve (build if not existant) {@link HbnFlexoConceptInstance} with supplied identifier asserting returned
		 * {@link HbnFlexoConceptInstance} object has supplied concept type and container<br>
		 * 
		 * This {@link HbnVirtualModelInstance} has an internal caching scheme allowing to store {@link HbnFlexoConceptInstance} relatively
		 * to their related {@link FlexoConcept} (their type) and their identifier
		 * 
		 * @param identifier
		 *            identifier for searched object
		 * @param container
		 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
		 * @param concept
		 *            type of returned {@link HbnFlexoConceptInstance}
		 * @return
		 */
		@Override
		public HbnFlexoConceptInstance getFlexoConceptInstance(Serializable identifier, FlexoConceptInstance container,
				FlexoConcept concept) {

			Map<Object, HbnFlexoConceptInstance> mapForConcept = instances.computeIfAbsent(concept, (newConcept) -> {
				return new HashMap<>();
			});

			return mapForConcept.computeIfAbsent(identifier, (newId) -> {
				Map<String, Object> hbnMap;
				try {
					hbnMap = (Map<String, Object>) getDefaultSession().get(concept.getName(), identifier);
					return getFactory().newFlexoConceptInstance(this, container, hbnMap, concept);
				} catch (HbnException e) {
					e.printStackTrace();
					return null;
				}
			});
		}

		/**
		 * Retrieve (build when non-existant) a list of {@link HbnFlexoConceptInstance} matching supplied {@link Query} asserting returned
		 * {@link HbnFlexoConceptInstance} objects have supplied concept type and container<br>
		 *
		 * @param query
		 *            query whose results have to be wrapped into {@link HbnFlexoConceptInstance} list
		 * @param container
		 *            container (eventually null) of returned {@link HbnFlexoConceptInstance}
		 * @param concept
		 *            type of returned {@link HbnFlexoConceptInstance}
		 * @return
		 */
		@Override
		public List<HbnFlexoConceptInstance> getFlexoConceptInstances(Query<?> query, FlexoConceptInstance container,
				FlexoConcept concept) {
			List<HbnFlexoConceptInstance> returned = new ArrayList<>();
			for (Object o : query.getResultList()) {
				if (o instanceof Map) {
					Map<String, Object> hbnMap = (Map<String, Object>) o;
					HbnFlexoConceptInstance fci = getFlexoConceptInstance(hbnMap, container, concept);
					returned.add(fci);
				}
			}
			return returned;
		}

		/**
		 * Instanciate and register a new {@link FlexoConceptInstance}
		 * 
		 * @param pattern
		 * @return
		 */
		@Override
		public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept) {

			return makeNewFlexoConceptInstance(concept, null);
		}

		/**
		 * Instantiate and register a new {@link FlexoConceptInstance} in a container FlexoConceptInstance
		 * 
		 * @param pattern
		 * @return
		 */
		@Override
		public HbnFlexoConceptInstance makeNewFlexoConceptInstance(FlexoConcept concept, FlexoConceptInstance container) {

			HbnFlexoConceptInstance returned = getResource().getFactory().newInstance(HbnFlexoConceptInstance.class, new HashMap<>(),
					concept);

			if (container != null) {
				container.addToEmbeddedFlexoConceptInstances(returned);
			}
			addToFlexoConceptInstances(returned);
			return returned;
		}

		@Override
		public Transaction beginTransaction() throws HbnException {
			if (getDefaultSession().getTransaction() == null) {
				return getDefaultSession().beginTransaction();
			}
			Transaction trans = getDefaultSession().getTransaction();
			if (trans.isActive()) {
				trans.commit();
			}
			trans.begin();
			return trans;
		}

		@Override
		public void commit() throws HbnException {
			if (getDefaultSession().getTransaction() == null) {
				throw new HbnException("No transaction associated to session");
			}
			getDefaultSession().getTransaction().commit();
		}

		@Override
		public void rollback() throws HbnException {
			if (getDefaultSession().getTransaction() == null) {
				throw new HbnException("No transaction associated to session");
			}
			getDefaultSession().getTransaction().rollback();
		}

	}
}
