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
    private final String authenticationScheme;
    private final String domain;
    private final String realm;
    private final String kdc;
    private final int timeout;

    public MSQLConnection(Properties properties) {
       this.host = properties.getProperty("host");
       this.port = properties.getProperty("port");
       this.instanceName = properties.getProperty("instance_name");
       this.databaseName = properties.getProperty("db_name");
       this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
       this.validateCertificates = Boolean.parseBoolean(properties.getProperty("validate_certificates"));
       this.authenticationScheme = properties.getProperty("authentication_scheme");
       this.username = properties.getProperty("username");
       this.password = properties.getProperty("password");
       this.domain  = properties.getProperty("domain");
       this.realm = properties.getProperty("realm");
       this.kdc = properties.getProperty("kdc");
       this.timeout = Integer.parseInt(properties.getProperty("timeout"));
        try {
            checkProps();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        LogSaver.appendLog(Level.INFO, "JDBC String: " + getConnectionString()+  "\n" +
                "User: " + username + "\n" +
                "Hostname: " + host);
    }


    @Override
    public String getConnectionString(){
        String connectionString =  SQLSERVER_PREFIX+host + ":" + port;
        System.out.println(connectionString);
        return connectionString;
    }

    private void checkProps() throws Exception {

        if (authenticationScheme.equals("kerberos")) {
            if (realm.isBlank() || realm.isEmpty()) {
                throw new Exception("Realm field can not be empty!");
            }
            if (kdc.isEmpty() || kdc.isBlank()) {
                throw new Exception("KDC can not be empty!");
            }
        }
    }
    @Override
    public Properties getProperties(){
        Properties properties = new Properties();

        properties.put("user", username);
        properties.put("password", password);

        properties.put("database", databaseName);
        properties.put("instanceName", instanceName);

        switch (authenticationScheme){
            case "basic":
                break;
            case "kerberos":
                properties.put("realm", realm);
                properties.put("kdc", kdc); //TODO
                break;
            case "ntlm":
                properties.put("domain", domain);
                break;
            default:
                System.out.println("Declare authentication scheme in properties: basic, kerberos, ntlm");
                System.exit(0);
        }
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
