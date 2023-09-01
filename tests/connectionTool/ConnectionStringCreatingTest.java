package connectionTool;

import connectionTool.endpoints.DB2Endpoint;
import connectionTool.endpoints.HanaDBEndpoint;
import connectionTool.endpoints.IConnection;
import connectionTool.endpoints.MSQLEndpoint;
import connectionTool.endpoints.MySQLEndpoint;
import connectionTool.endpoints.OracleEndpoint;
import connectionTool.endpoints.PostgreSQLEndpoint;
import connectionTool.endpoints.SnowflakeEndpoint;
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

        conn = new DB2Endpoint(properties);

        assertEquals("jdbc:db2://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testHanaDBConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("timeout", "10");

        conn = new HanaDBEndpoint(properties);

        assertEquals("jdbc:sap://123.456.789.0:1234", conn.getConnectionString());
    }
    @Test
    void testHanaMSQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("timeout", "10");

        conn = new MSQLEndpoint(properties);

        assertEquals("jdbc:sqlserver://123.456.789.0:1234", conn.getConnectionString());
    }
    @Test
    void testMySQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("db_name", "db");
        properties.put("timeout", "10");

        conn = new MySQLEndpoint(properties);

        assertEquals("jdbc:mariadb://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testOracleConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("service_name", "service");
        properties.put("timeout", "10");

        conn = new OracleEndpoint(properties);

        assertEquals("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL = TCP)(HOST=123.456.789.0)(PORT=1234))(CONNECT_DATA=(SERVICE_NAME=service)))", conn.getConnectionString());
    }
    @Test
    void testPostgreSQLConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("port", "1234");
        properties.put("db_name", "db");
        properties.put("timeout", "10");

        conn = new PostgreSQLEndpoint(properties);

        assertEquals("jdbc:postgresql://123.456.789.0:1234/db", conn.getConnectionString());
    }
    @Test
    void testSnowflakeConnection_shouldConnectionStringBeEqualsToGiven(){
        Properties properties = new Properties();
        properties.put("host", "123.456.789.0");
        properties.put("timeout", "10");

        conn = new SnowflakeEndpoint(properties);

        assertEquals("jdbc:snowflake://123.456.789.0", conn.getConnectionString());
    }
}
