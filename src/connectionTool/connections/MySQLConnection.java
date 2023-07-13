package connectionTool.connections;

import java.util.Properties;


public class MySQLConnection implements IConnection {

    private static final String MYSQL_PREFIX = "jdbc:mariadb://";
    private final String host;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private int timeout;

    public MySQLConnection(Properties properties) {
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.databaseName = properties.getProperty("db_name");
        this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(properties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout time to configuration");
            System.exit(0);
        }
        verify();
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
        properties.put("connectTimeout", timeout * 1000);
        if (sslEnabled) {
            properties.put("sslMode", "trust");
        }else {
            properties.put("sslMode", "disable");
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

    @Override
    public String getPort() {
        return port;
    }

    private void verify(){
        if (host == null || host.isEmpty()) {
            missingOrEmptyField("host");
        }
        if (port == null || port.isEmpty()) {
            missingOrEmptyField("port");
        }
        if (databaseName == null || databaseName.isEmpty()) {
            missingOrEmptyField("db_name");
        }
    }

    private void missingOrEmptyField(String field){
        System.out.println("Missing field: " + field);
        System.exit(0);
    }
}