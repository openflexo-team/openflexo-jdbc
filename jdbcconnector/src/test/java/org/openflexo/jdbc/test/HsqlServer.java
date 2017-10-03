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

package org.openflexo.jdbc.test;

import org.hsqldb.server.Server;

/**
 * Created by charlie on 18/10/2016.
 */
public class HsqlServer {

/*
Usage: java org.hsqldb.server.Server [options]

+-----------------+-------------+----------+------------------------------+
|     OPTION      |    TYPE     | DEFAULT  |         DESCRIPTION          |
+-----------------+-------------+----------+------------------------------|
| --help          | -           | -        | displays this message        |
| --address       | name|number | any      | server inet address          |
| --port          | number      | 9001/544 | port at which server listens |
| --database.i    | [type]spec  | 0=test   | name of database i           |
| --dbname.i      | alias       | -        | url alias for database i     |
| --silent        | true|false  | true     | false => display all queries |
| --trace         | true|false  | false    | display JDBC trace messages  |
| --tls           | true|false  | false    | TLS/SSL (secure) sockets     |
| --no_system_exit| true|false  | false    | do not issue System.exit()   |
| --remote_open   | true|false  | false    | can open databases remotely  |
| --props         | filepath    |          | file path of properties file |
+-----------------+-------------+----------+------------------------------+

The server looks for a 'server.properties' file in the current directory and
loads properties from it if it exists.
Command line options override those loaded from the 'server.properties' file.
*/

	public static void main(String[] args) {
		//String[] arguments = {"--database.0", "file:xdb", "--dbname.O", "xdb"};
		//String[] arguments = {"--help"};
		String[] arguments = {};
		Server.main(arguments);
	}
}
