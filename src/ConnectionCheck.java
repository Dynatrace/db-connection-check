/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ConnectionCheck.java
 * @date: Nov 25, 2015
 * @author: wiktor
 */

import java.io.File;
import java.net.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiktor
 */

public class ConnectionCheck {

	private static final String ORACLE_PREFIX = "jdbc:oracle:"; //$NON-NLS-1$
	private static final String SQLSERVER_PREFIX = "jdbc:sqlserver://"; //$NON-NLS-1$
	private static final String SQLSERVER_JTDS_PREFIX = "jdbc:jtds:sqlserver";//$NON-NLS-1$
	private static final String MYSQL_PREFIX = "jdbc:mysql";//$NON-NLS-1$
    private static final String MYSQL_LOADBALANCER_PREFIX = "jdbc:mysql:loadbalance://";//$NON-NLS-1$
    private static final String MYSQL_REPLICATION_PREFIX = "jdbc:mysql:replication://";//$NON-NLS-1$
    private static final String MYSQL_SPY = "jdbc:mysql_spy://";//$NON-NLS-1$
	private static final String HANA_DB_PREFIX = "jdbc:sap://";
	private static final String DB2_PREFIX = "jdbc:db2://";
	private static final String POSTGRESQL_PREFIX = "jdbc:postgresql://";
	private static final String SNOWFLAKE_PREFIX = "jdbc:snowflake://";
	private final static Logger LOGGER =
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(final String[] args) throws Exception {
		if (args.length > 5) {
			System.out.println("Please provide arguments: JDBC string, user name, password, timeout");
			System.exit(0);
		}
		String connectionString = args[0];
		final String user = args[1];
		final String password = args[2];
		final int timeout = Integer.valueOf(args[3]);
		final String host = getHostFromJdbcConnectionString(connectionString);
		String folderPath = "";
		if (args.length == 5) {
			folderPath = args[4];
		}else {
			folderPath = checkOs(extractProvider(connectionString));
		}


		LOGGER.log(Level.INFO, "JDBC String: " + connectionString +  "\n" +
				"User: " + user + "\n" +
				"Password: " + password + "\n" +
				"Timeout (seconds): " + timeout + "\n" +
				"Folder path: " + folderPath + "\n" +
				"Hostname: " + host);


		boolean isReachable = false;
		try {
			isReachable = InetAddress.getByName(host).isReachable(timeout * 1000);
		} catch (final UnknownHostException e) {
			isReachable = false;
			LOGGER.log(Level.WARNING, "InetAddress.getByName().isReachable(): got UnknownHostException");
		}

		LOGGER.log(Level.INFO, "InetAddress.getByName().isReachable(): " + isReachable);

		System.out.println("Attempting JDBC connection with timeout...");
		if(isSqlServer(connectionString)){
			connectionString += ";loginTimeout=" + String.valueOf(timeout * 1000);
		}
		final Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		connectionProps.put("oracle.net.CONNECT_TIMEOUT", String.valueOf(timeout * 1000));


		final Driver driver = findDriver(folderPath, extractProvider(connectionString));



		final Connection conn = driver.connect(connectionString, connectionProps);
		if (conn != null) {
			LOGGER.log(Level.INFO, "Connection through JDBC successfull!");
			conn.close();
		}
	}

	public static String getHostFromJdbcConnectionString(final String connectionString) {
		if (isOracle(connectionString)) {
			final int beginIndex = connectionString.indexOf("@") + 1;
			String hostPortSid = connectionString.substring(beginIndex);
			if (hostPortSid.startsWith("//")) {
				hostPortSid = hostPortSid.substring("//".length());
			}
			// handle new JDBC string format jdbc:oracle:thin:@//[HOST][:PORT]/SERVICE
			final String[] hostPortSidPartsFromNewFormat = hostPortSid.split("/");
			if (hostPortSidPartsFromNewFormat.length >= 2) {
				// stuff in front of the slash
				hostPortSid = hostPortSidPartsFromNewFormat[0];
			}
			final String[] hostPortPidParts = hostPortSid.split(":");
			return hostPortPidParts[0];
		} else if (isSqlServer(connectionString)) {
			String cutoffUrl = connectionString.toLowerCase();
			String splitter = ";";
			cutoffUrl = getCutoffUrl(cutoffUrl);

			if (cutoffUrl.startsWith("[")) {
				final int indexOfLastBracket = cutoffUrl.lastIndexOf("]");
				if (indexOfLastBracket > -1) {
					return cutoffUrl.substring(1, indexOfLastBracket);
				}
			}
			String[] split = null;
			if (cutoffUrl.indexOf("/") != -1) {
				splitter = "/";
			}
			split = cutoffUrl.split(splitter);
			final String host = split[0].split(":")[0];
			if (split.length > 0) {
				return host.split("\\\\")[0];
			}
			return cutoffUrl;
		} else if(isMySQL(connectionString)) {
            String url = connectionString.toLowerCase();
            String cutoffUrl = getCutoffUrl(url);
            String[] split = cutoffUrl.split("\\?");
            String replaced = split[0].replaceAll("/", ":");
            String[] hostSplit = replaced.split(":");
            if (hostSplit.length == 0) {
                return "localhost";
            }
            String hostname = hostSplit[0];
            if (hostname.indexOf(",") > 0) {
                hostname = hostname.split(",")[0];
            }

            return hostname;
        }
		else if (isHanaDB(connectionString)){

		}
		else if (isDB2(connectionString)){}
		else if (isPostgreSQL(connectionString)){}
		else if (isSnowflake(connectionString)){}

		return "";
	}

	private static boolean isOracle(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(ORACLE_PREFIX) &&
				connectionString.indexOf("@") > -1;
	}

	private static boolean isSqlServer(final String connectionString) {
		return connectionString != null && (connectionString.toLowerCase().startsWith(SQLSERVER_PREFIX) ||
				connectionString.toLowerCase().startsWith(SQLSERVER_JTDS_PREFIX));
	}

    private static boolean isMySQL(final String connectionString) {
        return connectionString != null && connectionString.toLowerCase().startsWith(MYSQL_PREFIX);
    }

	private static boolean isHanaDB(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(HANA_DB_PREFIX);
	}
	private static boolean isDB2(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(DB2_PREFIX);
	}
	private static boolean isPostgreSQL(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(POSTGRESQL_PREFIX);
	}
	private static boolean isSnowflake(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(SNOWFLAKE_PREFIX);
	}


    private static String getCutoffUrl(String url) {
        String cutoffUrl = url.substring(13, url.length());
        if (url.startsWith(MYSQL_LOADBALANCER_PREFIX)) {
            cutoffUrl = url.substring(MYSQL_LOADBALANCER_PREFIX.length(), url.length());
        }
        if (url.startsWith(MYSQL_REPLICATION_PREFIX)) {
            cutoffUrl = url.substring(MYSQL_REPLICATION_PREFIX.length(), url.length());
        }
        if (url.startsWith(MYSQL_SPY)) {
            cutoffUrl = url.substring(MYSQL_SPY.length(), url.length());
        }
		if (url.startsWith(ORACLE_PREFIX)) {
			cutoffUrl = url.substring(ORACLE_PREFIX.length(), url.length());
		}
		if (url.startsWith(SQLSERVER_PREFIX)) {
			cutoffUrl = url.substring(SQLSERVER_PREFIX.length(), url.length());
		}
		if (url.startsWith(SQLSERVER_JTDS_PREFIX)) {
			cutoffUrl = url.substring(SQLSERVER_JTDS_PREFIX.length(), url.length());
		}
		if (url.startsWith(MYSQL_PREFIX)) {
			cutoffUrl = url.substring(MYSQL_PREFIX.length(), url.length());
		}
		if (url.startsWith(HANA_DB_PREFIX)) {
			cutoffUrl = url.substring(HANA_DB_PREFIX.length(), url.length());
		}
		if (url.startsWith(DB2_PREFIX)) {
			cutoffUrl = url.substring(DB2_PREFIX.length(), url.length());
		}
		if (url.startsWith(POSTGRESQL_PREFIX)) {
			cutoffUrl = url.substring(POSTGRESQL_PREFIX.length(), url.length());
		}
		if (url.startsWith(SNOWFLAKE_PREFIX)) {
			cutoffUrl = url.substring(SNOWFLAKE_PREFIX.length(), url.length());
		}
        return cutoffUrl;
    }


	private static Driver findDriver(String folderPath, DataBase db) throws Exception {
		File file = new File(folderPath);
		System.out.println(file.getName());

		return Arrays.stream(Objects.requireNonNull(file.listFiles()))
				.filter(fl -> fl.getName().endsWith(".jar"))
				.map(fl -> {

							URL url = null;
							try {
								url = fl.toURL();
							} catch (MalformedURLException e) {
								System.out.println("Wrong driver's path");
							}
							URL[] urls = new URL[]{url};

							ClassLoader cl = new URLClassLoader(urls);
							try {
								return (Driver)Class.forName(getDriverClassName(db), true, cl).newInstance();

							} catch (ClassNotFoundException ignored) {

							} catch (InstantiationException e) {
								System.out.println("Can not instantiate the driver's class");
							} catch (IllegalAccessException e) {
								System.out.println("Can not access the driver's class");
							}
							return null;
						}
				).filter(Objects::nonNull).findFirst().orElseThrow(() -> new Exception("Couldn't find the driver"));

	}

	private static String checkOs(DataBase db){
		if (db != DataBase.HANA_DB)
		return System.getProperty("os.name").startsWith("Windows")
				? "C:/ProgramData/dynatrace/remotepluginmodule/agent/conf/java/libs/"
				: "/var/lib/dynatrace/remotepluginmodule/agent/conf/userdata/libs/";
		else return System.getProperty("os.name").startsWith("Windows")
				? "C:/ProgramData/dynatrace/remotepluginmodule/agent/res/userdata/libs/"
				: "/var/lib/dynatrace/remotepluginmodule/agent/res/userdata/libs/";
	}
	private static String getDriverClassName(DataBase dataBase){
		switch (dataBase){
			case MYSQL: return "org.mariadb.jdbc.Driver";
			case MICROSOFT: return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			case ORACLE: return "oracle.jdbc.driver.OracleDriver";
			case DB2: return "com.ibm.db2.jcc.DB2Driver";
			case HANA_DB: return "com.sap.db.jdbc.Driver";
			case JTDS: return "net.sourceforge.jtds.jdbc.Driver";
			case POSTGRESQL: return "org.postgresql.Driver";
			default: return "";
		}
	}

	private static DataBase extractProvider(String connectionString) throws Exception {
		if (connectionString.startsWith(ORACLE_PREFIX)){
			return DataBase.ORACLE;
		}
		if (connectionString.startsWith(SQLSERVER_PREFIX)
				|| connectionString.startsWith(SQLSERVER_JTDS_PREFIX)){
			return DataBase.MICROSOFT;
		}
		if (connectionString.startsWith(MYSQL_PREFIX) ||
				connectionString.startsWith(MYSQL_LOADBALANCER_PREFIX) ||
				connectionString.startsWith(MYSQL_REPLICATION_PREFIX) ||
				connectionString.startsWith(MYSQL_SPY)){
			return DataBase.MYSQL;
		}
		if (connectionString.startsWith(HANA_DB_PREFIX)){
			return DataBase.HANA_DB;
		}
		if (connectionString.startsWith(DB2_PREFIX)){
			return DataBase.DB2;
		}
		if (connectionString.startsWith(POSTGRESQL_PREFIX)){
			return DataBase.POSTGRESQL;
		}
		if (connectionString.startsWith(SNOWFLAKE_PREFIX)){
			return DataBase.SNOWFLAKE;
		}
		else throw new Exception();
	}


}
