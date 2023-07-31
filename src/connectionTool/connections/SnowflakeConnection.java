package connectionTool.connections;


import connectionTool.utills.Verifier;

import java.util.Properties;

public class SnowflakeConnection implements IConnection {

    private static final String SNOWFLAKE_PREFIX = "jdbc:snowflake://";
    private final String host;
    private final String port;
    private final String databaseName;
    private final String schema;
    private final String warehouse;
    private final String username;
    private final String password;
    private int timeout;

    public SnowflakeConnection(Properties properties) {
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.databaseName = properties.getProperty("db_name");
        this.schema = properties.getProperty("schema");
        this.warehouse = properties.getProperty("warehouse");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(properties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout time to configuration");
            System.exit(0);
        }
        Verifier.verifyConfig(this, "host");
    }

    @Override
    public String getConnectionString() {
        return SNOWFLAKE_PREFIX + host;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        properties.put("db", databaseName);
        properties.put("schema", schema);
        properties.put("warehouse", warehouse);
        properties.put("proxyPort", port);
        properties.put("loginTimeout", timeout);
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
