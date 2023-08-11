package connectionTool.endpoints;

import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostgreSQLEndpoint implements IConnection {

    private static final String POSTGRESQL_PREFIX = "jdbc:postgresql://";
    private final String host;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final String connectionString;
    private String port;
    private String databaseName;
    private int timeout;

    public PostgreSQLEndpoint(Properties configproperties) {
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);

        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.databaseName = configproperties.getProperty("databaseName");
        this.sslEnabled = Boolean.parseBoolean(configproperties.getProperty("ssl"));
        this.username = configproperties.getProperty("username");
        this.password = configproperties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout time to configuration");
            System.exit(0);
        }
        this.connectionString = createaConnectionString();
    }

    public PostgreSQLEndpoint(String connectionString, String username, String password, boolean isSSL, int timeout, String host) {
        this.connectionString = connectionString;
        this.username = username;
        this.password = password;
        this.sslEnabled = isSSL;
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
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("ssl", "true");
            properties.put("sslmode", "verify-full");
            properties.put("sslfactory","org.postgresql.ssl.DefaultJavaSSLFactory");
            System.setProperty("javax.net.ssl.trustStore", SSLConstant.getSSLTrustStorePath());
            System.setProperty("javax.net.ssl.trustStorePassword",SSLConstant.SSL_TRUSTSTORE_PASSWORD);
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
        return DatabaseProvider.POSTGRESQL;
    }

    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        requiredArguments.add("databaseName");
        return requiredArguments;
    }

    private String createaConnectionString(){
        if (port.isBlank()) {
            return POSTGRESQL_PREFIX + host + "/" + databaseName;
        } else {
            return POSTGRESQL_PREFIX + host + ":" + port + "/" + databaseName;
        }
    }
}
