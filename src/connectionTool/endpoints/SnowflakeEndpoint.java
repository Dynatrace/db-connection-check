package connectionTool.endpoints;


import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SnowflakeEndpoint implements IConnection {

    private static final String SNOWFLAKE_PREFIX = "jdbc:snowflake://";
    private final String host;
    private final String username;
    private final String password;
    private final String connectionString;
    private String port;
    private String databaseName;
    private String schema;
    private String warehouse;
    private int timeout;


    public SnowflakeEndpoint(Properties configproperties) {
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);

        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.databaseName = configproperties.getProperty("databaseName");
        this.schema = configproperties.getProperty("schema");
        this.warehouse = configproperties.getProperty("warehouse");
        this.username = configproperties.getProperty("username");
        this.password = configproperties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout time to configuration");
            System.exit(0);
        }
        this.connectionString = createConnectionString();
    }

    public SnowflakeEndpoint(String connectionString, String username, String password, int timeout, String host) {
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.connectionString = connectionString;
        this.host = host;
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public Properties getConnectionProperties() {
        Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (databaseName != null){
            properties.put("db", databaseName);
        }
        if (schema != null){
            properties.put("schema", schema);
        }
        if (warehouse != null){
            properties.put("warehouse", warehouse);
        }
        if (port != null){
            properties.put("proxyPort", port);
        }
        return properties;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getTimeoutInSeconds() {
        return timeout;
    }

    @Override
    public DatabaseProvider getProvider() {
        return DatabaseProvider.SNOWFLAKE;
    }
    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        return requiredArguments;
    }
    private String createConnectionString(){
        return SNOWFLAKE_PREFIX + host;
    }

}
