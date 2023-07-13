import connectionTool.connections.*;
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
    void testDriverLoader_shouldThrowNullPointerExceptionWhenWrongPathGiven(){
        String path = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\libos";

        assertThrows(NullPointerException.class, () -> DriverLoader.findDriver(path, Provider.DB2));
    }
    @Test
    void testDriverLoader_shouldThrowExceptionWhenDriverNotFound(){
        String path = "src/connection_tool/resources";

        assertThrows(DriverNotFoundException.class, () -> DriverLoader.findDriver(path, Provider.DB2));
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

        assertEquals("jdbc:oracle:thin:@123.456.789.0:1234/service", conn.getConnectionString());
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
