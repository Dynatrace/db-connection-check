import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

public class PostgreSQLConnection implements IConnection{

    private static final Scanner scanner = new Scanner(System.in);
    private static final String POSTGRESQL_PREFIX = "jdbc:postgresql://";
    private final String host;
    private final String port;
    private final String databaseName;
    private  final String username;
    private final String password;

    private final boolean sslEnabled;
    private final int timeout;




    public PostgreSQLConnection(Properties properties) {

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
        scanner.close();
    }

    @Override
    public String getConnectionString(){
        return POSTGRESQL_PREFIX+host + ":" + port +"/" + databaseName;
    }
    @Override
    public Properties getProperties(){
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("ssl", "true");
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
