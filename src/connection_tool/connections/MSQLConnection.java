package connection_tool.connections;

import connection_tool.LogSaver;

import java.util.Properties;
import java.util.logging.Level;

public class MSQLConnection implements IConnection {

    private static final String SQLSERVER_PREFIX = "jdbc:sqlserver://";
    private final String host;
    private final String port;
    private final String instanceName;
    private final String databaseName;
    private final boolean sslEnabled;
    private final boolean validateCertificates;

    private final String username;
    private final String password;
    private int timeout;

    public MSQLConnection(Properties properties) {
       this.host = properties.getProperty("host");
       this.port = properties.getProperty("port");
       this.instanceName = properties.getProperty("instance_name");
       this.databaseName = properties.getProperty("db_name");
       this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
       this.validateCertificates = Boolean.parseBoolean(properties.getProperty("validate_certificates"));
       this.username = properties.getProperty("username");
       this.password = properties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(properties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout time to configuration");
            System.exit(0);
        }

        LogSaver.appendLog(Level.INFO, "JDBC String: " + getConnectionString()+  "\n" +
                "User: " + username + "\n" +
                "Hostname: " + host);
    }


    @Override
    public String getConnectionString(){
        return SQLSERVER_PREFIX+host + ":" + port;
    }

    @Override
    public Properties getProperties(){
        Properties properties = new Properties();

        properties.put("user", username);
        properties.put("password", password);

        properties.put("database", databaseName);
        properties.put("instanceName", instanceName);
        properties.put("loginTimeout", String.valueOf(timeout));

        if (sslEnabled){
            properties.put("encrypt", "true");
        }
        if (validateCertificates){
            properties.put("trustServerCertificate", "true");
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
}
