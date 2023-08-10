package connectionTool;

import connectionTool.connections.DB2Connection;
import connectionTool.connections.HanaDBConnection;
import connectionTool.connections.IConnection;
import connectionTool.connections.MSQLConnection;
import connectionTool.connections.MySQLConnection;
import connectionTool.connections.OracleConnection;
import connectionTool.connections.DatabaseProvider;
import connectionTool.connections.PostgreSQLConnection;
import connectionTool.connections.SnowflakeConnection;
import connectionTool.utills.DriverLoader;
import connectionTool.exceptions.DriverNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConnectionStringCreatingTest {

    private IConnection conn;

    @Test
    void testDB2Connection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("db_name", "db");
        properties.put("timeout", "10");

        conn = new DB2Connection(properties);

        assertEquals("jdbc:db2://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testDriverLoader_shouldThrowNullPointerExceptionWhenInvalidPathGiven(){
        String path = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\libs";

        assertThrows(NullPointerException.class, () -> DriverLoader.findDriver(path, DatabaseProvider.DB2));
    }
    @Test
    void testDriverLoader_shouldThrowExceptionWhenDriverNotFound(){
        String path = "src/connectionTool/resources";

        assertThrows(DriverNotFoundException.class, () -> DriverLoader.findDriver(path, DatabaseProvider.DB2));
    }
    @Test
    void testHanaDBConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("timeout", "10");

        conn = new HanaDBConnection(properties);

        assertEquals("jdbc:sap://123.456.789.0:1234", conn.getConnectionString());
    }
    @Test
    void testHanaMSQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("timeout", "10");

        conn = new MSQLConnection(properties);

        assertEquals("jdbc:sqlserver://123.456.789.0:1234", conn.getConnectionString());
    }
    @Test
    void testMySQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("db_name", "db");
        properties.put("timeout", "10");

        conn = new MySQLConnection(properties);

        assertEquals("jdbc:mariadb://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testOracleConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("service_name", "service");
        properties.put("timeout", "10");

        conn = new OracleConnection(properties);

        assertEquals("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL = TCP)(HOST=123.456.789.0)(PORT=1234))(CONNECT_DATA=(SERVICE_NAME=service)))", conn.getConnectionString());
    }
    @Test
    void testPostgreSQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("db_name", "db");
        properties.put("timeout", "10");

        conn = new PostgreSQLConnection(properties);

        assertEquals("jdbc:postgresql://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testSnowflakeConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("timeout", "10");

        conn = new SnowflakeConnection(properties);

        assertEquals("jdbc:snowflake://123.456.789.0", conn.getConnectionString());
    }
}
