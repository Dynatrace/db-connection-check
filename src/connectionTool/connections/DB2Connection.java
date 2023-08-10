package connectionTool.connections;

import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;
import java.util.Properties;

public class DB2Connection implements IConnection {

    private static final String DB2_PREFIX = "jdbc:db2://";
    private final String host;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private int timeout;

    public DB2Connection(Properties configproperties) {
        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.databaseName = configproperties.getProperty("db_name");
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
        Verifier.verifyConfig(this, "host","port","databaseName");
    }

    @Override
    public String getConnectionString(){
        return DB2_PREFIX+host + ":" + port +"/" + databaseName;
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
}
