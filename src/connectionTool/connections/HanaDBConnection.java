package connectionTool.connections;


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
        verify();
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
        properties.put("timeout", timeout);
        if (sslEnabled) {
            properties.put("encrypt", "true");
            properties.put("validateCertificate", "true");
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
    }

    private void missingOrEmptyField(String field){
        System.out.println("Missing field: " + field);
        System.exit(0);
    }
}
