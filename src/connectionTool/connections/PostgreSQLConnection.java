package connectionTool.connections;

import connectionTool.utills.Verifier;

import java.util.Properties;

public class PostgreSQLConnection implements IConnection {

    private static final String POSTGRESQL_PREFIX = "jdbc:postgresql://";
    private final String host;
    private final String port;
    private final String databaseName;
    private  final String username;
    private final String password;
    private final boolean sslEnabled;
    private int timeout;


    public PostgreSQLConnection(Properties properties) {

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
        Verifier.verifyConfig(this, "host","db_name");
    }

    @Override
    public String getConnectionString(){
        if(!port.isBlank())
            return POSTGRESQL_PREFIX+host + ":" + port +"/" + databaseName;
        else {
            return POSTGRESQL_PREFIX + host + "/" + databaseName;
        }
    }
    @Override
    public Properties getProperties(){
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        properties.put("loginTimeout", timeout);
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

    @Override
    public String getPort() {
        return port;
    }
}
