package org.openflexo.technologyadapter.jdbc.model;

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
