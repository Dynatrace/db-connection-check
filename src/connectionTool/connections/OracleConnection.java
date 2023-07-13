package connectionTool.connections;


import java.util.Properties;

public class OracleConnection implements IConnection {


    private static final String PREFIX = "jdbc:oracle:thin:@";
    private final String host;
    private final String port;
    private final String serviceName;
    private final String sid;
    private final String username;
    private final String password;
    private int timeout;
    private final boolean sslEnabled;

    public OracleConnection(Properties properties){
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.serviceName = properties.getProperty("service_name");
        this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.sid = properties.getProperty("sid");
        try {
            timeout = Integer.parseInt(properties.getProperty("timeout"));
        }catch (NumberFormatException e){
            System.out.println("Add timeout to configuration");
            System.exit(0);
        }
        verify();
    }

    @Override
    public String getConnectionString(){
        if (sid == null || sid.isEmpty()){
            return PREFIX + host + ":" + port + "/" + serviceName;

        }else {
            return PREFIX + host + ":" + port + ":" + sid;
        }
    }
    @Override
    public Properties getProperties(){
        Properties properties = new Properties();
        properties.put ("user", username);
        properties.put ("password",password);
        properties.put("oracle.net.CONNECT_TIMEOUT", timeout);
        if (sslEnabled){
            properties.put("CONNECTION_PROPERTY_THIN_SSL_SERVER_DN_MATCH","true");
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
        if (!(serviceName == null || serviceName.isEmpty()) && !(sid == null || sid.isEmpty())){
            System.out.println(("Service name and sid can not be both assigned"));
            System.exit(0);
        }
        if ((serviceName == null || serviceName.isEmpty()) && (sid == null || sid.isEmpty())){
            System.out.println(("Missing field: service_name or sid"));
            System.exit(0);
        }
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
