import java.util.Properties;
import java.util.logging.Level;

public class OracleConnection implements IConnection {


    private static final String PREFIX = "jdbc:oracle:thin:@";
    private final String host;
    private final String port;
    private final String serviceName;
    private final String sid;
    private final String username;
    private final String password;
    private final int timeout;
    private final boolean sslEnabled;

    public OracleConnection(Properties properties){
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.serviceName = properties.getProperty("service_name");
        this.sslEnabled = Boolean.parseBoolean(properties.getProperty("ssl"));
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.sid = properties.getProperty("sid");
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
        if (sid.isBlank()){
            return PREFIX + host + ":" + port + ":" + serviceName;

        }else {
            return PREFIX + host + ":" + port + ":" + sid;
        }
    }

    private void checkProps() throws Exception {
        if (!serviceName.isBlank() && !sid.isBlank()){
            throw new Exception("Service name and sid can not be both assigned ");
        }
    }


    @Override
    public Properties getProperties(){
        Properties properties = new Properties();
        properties.put ("user", username);
        properties.put ("password",password);

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
