package connection_tool.connections;

import java.util.Properties;

public interface IConnection {

    String getConnectionString();
    Properties getProperties();
    String getHost();
    int getTimeout();
}