package connectionTool.endpoints;


import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HanaDBEndpoint implements IConnection {

    private static final String HANA_DB_PREFIX = "jdbc:sap://";
    private final String host;
    private String port;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private int timeout;
    private final String connectionString;
    private String databaseName;

    public HanaDBEndpoint(Properties configproperties) {
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);
        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.sslEnabled = Boolean.parseBoolean(configproperties.getProperty("ssl"));
        this.username = configproperties.getProperty("username");
        this.password = configproperties.getProperty("password");
        this.databaseName = configproperties.getProperty("databaseName");
        this.timeout = 0;
        try {
            this.timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }

        connectionString = createConnectionString();
    }

    public HanaDBEndpoint(String connectionString, String username, String password, boolean sslEnabled, int timeout, String host) {
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
        this.sslEnabled = sslEnabled;
        this.timeout = timeout;
        this.host = host;
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public Properties getConnectionProperties() {
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("encrypt", "true");
            properties.put("databaseName", databaseName);
            properties.put("validateCertificate", "true");
            properties.put("trustStore", SSLConstant.getSSLTrustStorePath());
            properties.put("trustStorePassword", SSLConstant.SSL_TRUSTSTORE_PASSWORD);
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
        return DatabaseProvider.HANA_DB;
    }

    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        requiredArguments.add("port");
        return requiredArguments;
    }
    private String createConnectionString(){
        return HANA_DB_PREFIX + host + ":" + port;
    }
}
