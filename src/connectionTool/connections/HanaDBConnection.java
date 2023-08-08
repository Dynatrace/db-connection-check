package connectionTool.connections;


import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;
import java.util.Properties;

public class HanaDBConnection implements IConnection {

    private static final String HANA_DB_PREFIX = "jdbc:sap://";
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private int timeout;
    public HanaDBConnection(Properties properties) {
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        
        this.timeout = 0;
        try {
            this.timeout = Integer.parseInt(properties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }
        Verifier.verifyConfig(this, "host","port");
    }


    @Override
    public String getConnectionString() {
        return HANA_DB_PREFIX + host + ":" + port;
    }

    @Override
    public Properties getProperties() {
        var properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        if (sslEnabled) {
            properties.put("encrypt", "true");
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
    public int getTimeout() {
        return timeout;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public Provider getProvider() {
        return Provider.HANA_DB;
    }
}
