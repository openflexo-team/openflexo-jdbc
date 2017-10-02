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

package org.openflexo.technologyadapter.jdbc.model;

/**
 * List of Database Types known to Openflexo
 * 
 * @author xtof
 *
 */
public enum JDBCDbType {
	HSQLDB // Hsql in memory dabatase server
	{
		@Override
		public String getDriverClassName() {
			return "org.hsqldb.jdbcDriver";
		}

		@Override
		public String getHibernateDialect() {
			return "org.hibernate.dialect.HSQLDialect";
		}

		@Override
		public String getSchemaPattern() {
			return "PUBLIC";
		}
	},
	POSTGRESQL // POSTGRESQL Dababase
	{
		@Override
		public String getDriverClassName() {
			return "org.postgresql.Driver";

		}

		@Override
		public String getHibernateDialect() {
			return "org.hibernate.dialect.PostgreSQLDialect";
		}

		@Override
		public String getSchemaPattern() {
			return "%";
		}
	},
	SQLSERVER // Microsoft SQL Server
	{
		@Override
		public String getDriverClassName() {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		}

		@Override
		public String getHibernateDialect() {
			return "org.hibernate.dialect.SQLServerDialect";
		}

		@Override
		public String getSchemaPattern() {
			return "dbo";
		}
	},
	GENERIC // to enable testing with other database not completely supported
	{
		@Override
		public String getDriverClassName() {
			return null;
		}

		@Override
		public String getHibernateDialect() {
			return null;
		}

		@Override
		public String getSchemaPattern() {
			return "%";
		}
	};

	/**
	 * 
	 * Returns a name for the JDBC Driver class to use or null if none is known
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	abstract public String getDriverClassName();

	/**
	 * 
	 * Returns a name for the Hibernate Dialect class to use for the given type
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	abstract public String getHibernateDialect();

	/**
	 * 
	 * Returns a name for the Hibernate Dialect class to use for the given type
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	abstract public String getSchemaPattern();
}
