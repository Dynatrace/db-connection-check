import java.util.Properties;
import java.util.logging.Level;


public class MySQLConnection implements IConnection {

    private static final String MYSQL_PREFIX = "jdbc:mysql";
    private final String host;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final int timeout;

    public MySQLConnection(Properties properties) {
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.databaseName = properties.getProperty("db_name");
        this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.timeout = Integer.parseInt(properties.getProperty("timeout"));


        LogSaver.appendLog(Level.INFO, "JDBC String: " + getConnectionString()+  "\n" +
                "User: " + username + "\n" +
                "Hostname: " + host);
    }

    @Override
    public String getConnectionString() {
        return MYSQL_PREFIX + host + ":" + port + "/" + databaseName;
    }

    @Override
    public Properties getProperties() {
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("useSSL", "true");
        }
        return properties;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}
