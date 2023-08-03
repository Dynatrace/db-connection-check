package connectionTool.connections;

import java.util.Properties;

public interface IConnection {

    String getConnectionString();
    Properties getProperties();
    String getHost();
    int getTimeout();
    String getPort();
    Provider getProvider();
}