package connectionTool.connections;
/***************************************************
 * dynaTrace Diagnostics (c) dynaTrace software GmbH
 *
 * @file: connection_tool.connections.ConnectionCheck.java
 * @date: Nov 25, 2015
 * @author: wiktor
 */

import connectionTool.utills.LogSaver;

import java.util.Properties;

import static connectionTool.constants.ConnectionConstant.*;


/**
 *
 * @author wiktor
 */

public class ConnectionCheck{

	private final String connectionString;
	private final String user;
	private final String password;
	private final String host;
	private final int timeout;

	public ConnectionCheck(String connectionString, String user, String password, int timeout) {
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
		this.timeout = timeout;

		host = getHostFromJdbcConnectionString(connectionString);

		LogSaver.appendLog("JDBC String: " + connectionString +  " " +
				"User: " + user + " " +
				"Hostname: " + host);
	}

	public String getConnectionString() {
		return connectionString;
	}

	public String getHost() {
		return host;
	}

	public Properties getProperties(){
		final Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		return connectionProps;
	}

	public Provider getProvider(){
		Provider provider = extractProvider(connectionString);
		if (provider == null){
			System.out.println("Couldn't resolve provider");
			LogSaver.appendLog("Couldn't resolve provider");
			System.exit(0);
		}
		return provider;
	}
	public int getTimeout(){
		return timeout;
	}

	private  String getHostFromJdbcConnectionString(final String connectionString) {
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
			String[] split;
			if (cutoffUrl.contains("/")) {
				splitter = "/";
			}
			split = cutoffUrl.split(splitter);
			final String host = split[0].split(":")[0];
			if (split.length > 0) {
				return host.split("\\\\")[0];
			}
			return cutoffUrl;
		} else if(isMySQL(connectionString)) {
			return extractHostAddress(connectionString, MYSQL_PREFIX);
		}
		else if (isHanaDB(connectionString)){
			return extractHostAddress(connectionString, HANA_DB_PREFIX);
		}
		else if (isDB2(connectionString)){
			return extractHostAddress(connectionString, DB2_PREFIX);
		}
		else if (isPostgreSQL(connectionString)){
			return extractHostAddress(connectionString, POSTGRESQL_PREFIX);
		}
		else if (isSnowflake(connectionString)){
			return extractHostAddress(connectionString, SNOWFLAKE_PREFIX);
		}

		return "";
	}

	private boolean isOracle(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(ORACLE_PREFIX) &&
				connectionString.contains("@");
	}

	private boolean isSqlServer(final String connectionString) {
		return connectionString != null && (connectionString.toLowerCase().startsWith(SQLSERVER_PREFIX));
	}

	private boolean isMySQL(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(MYSQL_PREFIX);
	}

	private boolean isHanaDB(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(HANA_DB_PREFIX);
	}
	private boolean isDB2(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(DB2_PREFIX);
	}
	private boolean isPostgreSQL(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(POSTGRESQL_PREFIX);
	}
	private boolean isSnowflake(final String connectionString) {
		return connectionString != null && connectionString.toLowerCase().startsWith(SNOWFLAKE_PREFIX);
	}


	private String getCutoffUrl(String url) {
		String cutoffUrl = url;
		if (url.startsWith(MYSQL_LOADBALANCER_PREFIX)) {
			cutoffUrl = url.substring(MYSQL_LOADBALANCER_PREFIX.length());
		}
		if (url.startsWith(MYSQL_REPLICATION_PREFIX)) {
			cutoffUrl = url.substring(MYSQL_REPLICATION_PREFIX.length());
		}
		if (url.startsWith(MYSQL_SPY)) {
			cutoffUrl = url.substring(MYSQL_SPY.length());
		}
		if (url.startsWith(ORACLE_PREFIX)) {
			cutoffUrl = url.substring(ORACLE_PREFIX.length());
		}
		if (url.startsWith(SQLSERVER_PREFIX)) {
			cutoffUrl = url.substring(SQLSERVER_PREFIX.length());
		}
		if (url.startsWith(MYSQL_PREFIX)) {
			cutoffUrl = url.substring(MYSQL_PREFIX.length());
		}
		if (url.startsWith(HANA_DB_PREFIX)) {
			cutoffUrl = url.substring(HANA_DB_PREFIX.length());
		}
		if (url.startsWith(DB2_PREFIX)) {
			cutoffUrl = url.substring(DB2_PREFIX.length());
		}
		if (url.startsWith(POSTGRESQL_PREFIX)) {
			cutoffUrl = url.substring(POSTGRESQL_PREFIX.length());
		}
		if (url.startsWith(SNOWFLAKE_PREFIX)) {
			cutoffUrl = url.substring(SNOWFLAKE_PREFIX.length());
		}
		return cutoffUrl;
	}





	private Provider extractProvider(String connectionString) {
		if (connectionString.startsWith(ORACLE_PREFIX)){
			return Provider.ORACLE;
		}
		if (connectionString.startsWith(SQLSERVER_PREFIX)){
			return Provider.MICROSOFT;
		}
		if (connectionString.startsWith(MYSQL_PREFIX) ||
				connectionString.startsWith(MYSQL_LOADBALANCER_PREFIX) ||
				connectionString.startsWith(MYSQL_REPLICATION_PREFIX) ||
				connectionString.startsWith(MYSQL_SPY)){
			return Provider.MYSQL;
		}
		if (connectionString.startsWith(HANA_DB_PREFIX)){
			return Provider.HANA_DB;
		}
		if (connectionString.startsWith(DB2_PREFIX)){
			return Provider.DB2;
		}
		if (connectionString.startsWith(POSTGRESQL_PREFIX)){
			return Provider.POSTGRESQL;
		}
		if (connectionString.startsWith(SNOWFLAKE_PREFIX)){
			return Provider.SNOWFLAKE;
		}
		else return null;
	}
	private String extractHostAddress(String connectionString, String prefix){
		String url = connectionString.toLowerCase();
		url = connectionString.substring(prefix.length(), url.length());
		String[] split = url.split("\\?");
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


}
