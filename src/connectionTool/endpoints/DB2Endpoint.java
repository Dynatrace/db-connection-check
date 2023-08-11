package connectionTool.endpoints;

import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DB2Endpoint implements IConnection {

    private static final String DB2_PREFIX = "jdbc:db2://";
    private final String host;
    private final String connectionString;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private String port;
    private  String databaseName;
    private int timeout;

    public DB2Endpoint(Properties configproperties) {
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);
        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.databaseName = configproperties.getProperty("databaseName");
        this.sslEnabled = Boolean.parseBoolean(configproperties.getProperty("ssl"));
        this.username = configproperties.getProperty("username");
        this.password = configproperties.getProperty("password");
        this.timeout = 0;
        try {
            this.timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }

        connectionString = createConnectionString();
    }

    public DB2Endpoint(String connectionString, String username, String password, boolean sslEnabled, int timeout, String host) {
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
        this.sslEnabled = sslEnabled;
        this.timeout = timeout;
        this.host = host;
    }

    @Override
    public String getConnectionString(){
        return connectionString;
    }
    @Override
    public Properties getConnectionProperties(){
        var properties = new Properties();
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        if (sslEnabled) {
            properties.setProperty("sslConnection", "true");
            properties.setProperty("sslTrustStoreLocation", SSLConstant.getSSLTrustStorePath());
            properties.setProperty("sslTrustStorePassword", SSLConstant.SSL_TRUSTSTORE_PASSWORD);
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
        return DatabaseProvider.DB2;
    }

    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        requiredArguments.add("port");
        requiredArguments.add("databaseName");
        return requiredArguments;
    }
    private String createConnectionString(){
        return DB2_PREFIX + host + ":" + port +"/" + databaseName;
    }

}
