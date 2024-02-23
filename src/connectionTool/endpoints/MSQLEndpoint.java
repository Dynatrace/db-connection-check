package connectionTool.endpoints;


import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MSQLEndpoint implements IConnection {

    private static final String SQLSERVER_PREFIX = "jdbc:sqlserver://";
    private final String host;
    private final boolean sslEnabled;
    private final boolean validateCertificates;
    private final String connectionString;
    private String port;
    private String instanceName;
    private String databaseName;
    private final String username;
    private final String password;
    private int timeout;

    public MSQLEndpoint(Properties configproperties) {
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);

       this.host = configproperties.getProperty("host");
       this.port = configproperties.getProperty("port");
       this.instanceName = configproperties.getProperty("instanceName");
       this.databaseName = configproperties.getProperty("databaseName");
       this.sslEnabled = Boolean.parseBoolean(configproperties.getProperty("ssl"));
       this.validateCertificates = Boolean.parseBoolean(configproperties.getProperty("validateCertificates"));
       this.username = configproperties.getProperty("username");
       this.password = configproperties.getProperty("password");
        try {
            this.timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }
        connectionString = createConnectionString();
    }



    public MSQLEndpoint(String connectionString, String username, String password, boolean isSSL, int timeout, boolean validateCertificates, String host) {
        this.connectionString = connectionString;
        this.sslEnabled = isSSL;
        this.validateCertificates = validateCertificates;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.host = host;
    }

    @Override
    public String getConnectionString(){
        return connectionString;
    }

    @Override
    public Properties getConnectionProperties(){
        Properties properties = new Properties();

        properties.put("user", username);
        properties.put("password", password);
        if(databaseName != null){
            properties.put("database", databaseName);
        }
        if (instanceName != null){
            properties.put("instanceName", instanceName);
        }
        if (sslEnabled){
            properties.put("encrypt", "true");
            properties.put("trustServerCertificate", "true");
            if (!validateCertificates){
                properties.put("trustServerCertificate", "false");
                properties.put("trustStore", SSLConstant.getSSLTrustStorePath());
                properties.put("trustStorePassword", SSLConstant.SSL_TRUSTSTORE_PASSWORD);
            }
        }else {
            properties.put("encrypt","false");
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
        return DatabaseProvider.MICROSOFT;
    }
    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        return requiredArguments;
    }

    private Boolean isPort() {
        if (port == null || port.isEmpty())
            return false;
        else
            return true;


    }

    private String createConnectionString(){
        if (!isPort() && (instanceName == null || instanceName.isEmpty())) {
			return SQLSERVER_PREFIX+host;
		}
		if (isPort() && (instanceName == null || instanceName.isEmpty())) {
			return SQLSERVER_PREFIX+host + ":" + port;
		}
		if (!isPort()) {
			return SQLSERVER_PREFIX+host + "\\" + instanceName;
		}
		return SQLSERVER_PREFIX+host + "\\" + instanceName + ":" + port;
    }
}
