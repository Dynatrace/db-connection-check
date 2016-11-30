/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: ConnectionCheck.java
 * @date: Nov 25, 2015
 * @author: wiktor
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

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

	public static void main(final String[] args) throws Exception {
		if (args.length != 4) {
			System.out.println("Please provide arguments: JDBC string, user name, password, timeout");
			System.exit(0);
		}
		String connectionString = args[0];
		final String user = args[1];
		final String password = args[2];
		final int timeout = Integer.valueOf(args[3]);

		System.out.println("JDBC String: " + connectionString);
		System.out.println("User: " + user);
		System.out.println("Password: " + password);
		System.out.println("Timeout (seconds): " + timeout);
		final String host = getHostFromJdbcConnectionString(connectionString);

		System.out.println("Hostname: " + host);

		boolean isReachable = false;
		try {
			isReachable = InetAddress.getByName(host).isReachable(timeout * 1000);
		} catch (final UnknownHostException e) {
			isReachable = false;
			System.out.println("InetAddress.getByName().isReachable(): got UnknownHostException");
		}

		System.out.println("InetAddress.getByName().isReachable(): " + isReachable);

		System.out.println("Attempting JDBC connection with timeout...");
		if(isSqlServer(connectionString)){
			connectionString += ";loginTimeout=" + String.valueOf(timeout * 1000);
		}
		final Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		connectionProps.put("oracle.net.CONNECT_TIMEOUT", String.valueOf(timeout * 1000));
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("loaded Oracle driver");
		} catch (final Exception e) {
			System.out.println("failed to load Oracle driver");
		}
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			System.out.println("loaded jTDS driver");
		} catch (final Exception e) {
			System.out.println("failed to load jTDS driver");
		}
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			System.out.println("loaded Microsoft SQLSERVER driver");
		} catch (final Exception e) {
			System.out.println("failed to load Microsoft SQLSERVER driver");
		}
        try {
            Class.forName("org.mariadb.jdbc.Driver");
			System.out.println("loaded MySQL (MariaDB) driver");
        } catch (final Exception e) {
            System.out.println("failed to load MySQL (MariaDB) driver");
        }

		final Connection conn = DriverManager.getConnection(connectionString, connectionProps);
		if (conn != null) {
			System.out.println("Connection through JDBC successfull!");
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
			if (cutoffUrl.startsWith(SQLSERVER_JTDS_PREFIX + "://")) {
				cutoffUrl = connectionString.substring(
						SQLSERVER_JTDS_PREFIX.length() + "://".length(), connectionString.length());
			} else if (cutoffUrl.startsWith(SQLSERVER_JTDS_PREFIX + ":/")) {
				cutoffUrl = connectionString.substring(
						SQLSERVER_JTDS_PREFIX.length() + ":/".length(), connectionString.length());
			} else if (cutoffUrl.startsWith(SQLSERVER_PREFIX)) {
				cutoffUrl = connectionString.substring(SQLSERVER_PREFIX.length(), connectionString.length());
			}

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
            String cutoffUrl = getMysqlCutoffUrl(url);
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

    private static String getMysqlCutoffUrl(String url) {
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
        return cutoffUrl;
    }


}
