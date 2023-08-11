package connectionTool.endpoints;

import java.util.Properties;

public interface IConnection {

    String getConnectionString();
    Properties getConnectionProperties();
    String getHost();
    int getTimeoutInSeconds();
    DatabaseProvider getProvider();
}