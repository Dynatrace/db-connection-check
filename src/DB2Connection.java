import java.util.Properties;
import java.util.logging.Level;

public class DB2Connection implements IConnection {

    private static final String DB2_PREFIX = "jdbc:db2://";
    private final String host;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final int timeout;


    public DB2Connection(Properties properties) {
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
    public String getConnectionString(){
        return DB2_PREFIX+host + ":" + port +"/" + databaseName;
  //      return "jdbc:db2://db2DateInstance.lab.dynatrace.org:25001/SAMPLE:sslConnection=true;";
    }
    @Override
    public Properties getProperties(){
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("sslConnection", "true");
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
