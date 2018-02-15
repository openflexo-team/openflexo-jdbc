package org.openflexo.jdbc.hbn;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.openflexo.connie.hbn.HbnConfig;

//TODO: to be removed: used for experimentations
@Ignore
public class MyTest {

	protected final static String jdbcURL = "jdbc:hsqldb:hsql://localhost/";
	protected final static String jdbcDriverClassname = "org.hsqldb.jdbcDriver";
	protected final static String jdbcUser = "sa";
	protected final static String jdbcPwd = "";

	protected final static String hbnDialect = "org.hibernate.dialect.HSQLDialect";
	protected HbnConfig config = null;

	@Test
	public void connectToDB() throws ClassNotFoundException, SQLException {

		// Loads JdbcDriver
		Class.forName(jdbcDriverClassname);

		try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd)) {

			if (conn != null) {
				System.out.println("Got Connection.");

				// Setup Hibernate config

				config = new HbnConfig(new BootstrapServiceRegistryBuilder().build());

				config.setProperty("hibernate.connection.driver_class", jdbcDriverClassname);
				config.setProperty("hibernate.connection.url", jdbcURL);
				config.setProperty("hibernate.connection.username", jdbcUser);
				config.setProperty("hibernate.connection.password", jdbcPwd);
				config.setProperty("hibernate.connection.pool_size", "1");
				config.setProperty("hibernate.dialect", hbnDialect);
				config.setProperty("hibernate.show_sql", "true");
				// creates object, wipe out if already exists
				config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

				DatabaseMetaData meta = conn.getMetaData();

				try (ResultSet schemas = meta.getSchemas()) {
					while (schemas.next()) {
						System.out.println("****** Schema");
						String tableSchema = schemas.getString(1); // "TABLE_SCHEM"
						String tableCatalog = schemas.getString(2); // "TABLE_CATALOG"
						System.out.println("tableSchema " + tableSchema);

						try (ResultSet tables = meta.getTables(tableCatalog, tableSchema, "%", null)) {
							while (tables.next()) {
								String val1 = tables.getString(1);
								String val2 = tables.getString(2);
								String val3 = tables.getString(3);
								System.out.println("tableSchema " + val1 + "-- " + val2 + "--" + val3);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			fail("Cannot connect to DB: " + jdbcURL);
			e.printStackTrace();
		}
	}
}
