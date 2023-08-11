package connectionTool.endpoints;


import connectionTool.constants.SSLConstant;
import connectionTool.utills.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OracleEndpoint implements IConnection {

    private static final String PREFIX = "jdbc:oracle:thin:@";
    private final String host;
    private final String username;
    private final String password;
    private final boolean sslEnabled;
    private final String connectionString;
    private String port;
    private String serviceName;
    private String sid;
    private int timeout;

    public OracleEndpoint(Properties configproperties){
        Verifier.verifyConfig(getRequiredConfigValuesList(), configproperties);
        this.host = configproperties.getProperty("host");
        this.port = configproperties.getProperty("port");
        this.serviceName = configproperties.getProperty("serviceName");
        this.sslEnabled = Boolean.parseBoolean(configproperties.getProperty("ssl"));
        this.username = configproperties.getProperty("username");
        this.password = configproperties.getProperty("password");
        this.sid = configproperties.getProperty("sid");
        try {
            timeout = Integer.parseInt(configproperties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }
        verify();
        this.connectionString = createConnectionString();
    }

    public OracleEndpoint(String connectionString, String username, String password, boolean isSSL, int timeout, String host) {
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
        Properties properties = new Properties();
        properties.put ("user", username);
        properties.put ("password",password);
        if (sslEnabled){
            properties.put("oracle.net.ssl_server_dn_match","true");
            System.setProperty("javax.net.ssl.trustStorePassword", SSLConstant.SSL_TRUSTSTORE_PASSWORD);
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.trustStore",SSLConstant.getSSLTrustStorePath());
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
        return DatabaseProvider.ORACLE;
    }

    private void verify(){
        if (!(serviceName == null || serviceName.isEmpty()) && !(sid == null || sid.isEmpty())){
            System.out.println(("Service name and sid can not be both assigned"));
            System.exit(0);
        }
        if ((serviceName == null || serviceName.isEmpty()) && (sid == null || sid.isEmpty())){
            System.out.println(("Missing field: service_name or sid"));
            System.exit(0);
        }
    }


    private String createConnectionString(){
        String protocol = sslEnabled ? "(PROTOCOL=tcps)" : "(PROTOCOL = TCP)";
        String conData = serviceName.isEmpty() ? "(SID=" + sid +")" : "(SERVICE_NAME=" + serviceName +")";
        return PREFIX + "(DESCRIPTION=(ADDRESS=" + protocol + "(HOST="+ host + ")(PORT=" + port +"))(CONNECT_DATA=" + conData + "))";
    }

    private List<String> getRequiredConfigValuesList(){
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add("host");
        requiredArguments.add("port");
        return requiredArguments;
    }
}
