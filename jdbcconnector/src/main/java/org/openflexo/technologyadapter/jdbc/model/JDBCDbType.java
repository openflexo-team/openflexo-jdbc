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

public final class JDBCDbType {

	/**
	 * List of Database Types known to Openflexo
	 * 
	 * @author xtof
	 *
	 */
	public enum DbType {
		HSQLDB, // Hsql in memory dabatase server
		POSTGRESQL,
		SQLSERVER, // Microsoft SQL Server
		GENERIC // to enable testing with other database not completely supported
	}

	/**
	 * 
	 * Returns a name for the JDBC Driver class to use or null if none is known
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	public static String getDriverClassName(DbType d) {
		switch (d) {
			case HSQLDB:
				return "org.hsqldb.jdbcDriver";
			case POSTGRESQL:
				return "org.postgresql.Driver";
			case SQLSERVER:
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			case GENERIC:
				return null;
			default:
				return null;
		}
	}

	/**
	 * 
	 * Returns a name for the Hibernate Dialect class to use for the given type
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	public static String getHibernateDialect(DbType d) {
		switch (d) {
			case HSQLDB:
				return "org.hibernate.dialect.HSQLDialect";
			case POSTGRESQL:
				return "org.hsqldb.PostgreSQLDialect";
			case SQLSERVER:
				return "org.hibernate.dialect.SQLServerDialect";
			case GENERIC:
				return null;
			default:
				return null;
		}
	}

	/**
	 * 
	 * Returns a name for the Hibernate Dialect class to use for the given type
	 * 
	 * @param d,
	 *            Database Type
	 * @return
	 */
	public static String getSchemaPattern(DbType d) {
		switch (d) {
			case HSQLDB:
				return "PUBLIC";
			case POSTGRESQL:
				return "%";
			case SQLSERVER:
				return "dbo";
			case GENERIC:
				return "%";
			default:
				return "%";
		}
	}

}
