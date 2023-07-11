import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);
    private static IConnection dbConn;

    public static void main(String[] args) {
        run();
    }

    private static void run(){
        String folderPath = null;
        System.out.println("Use custom folder path?\n1) Yes\n2) No");
        switch (scanner.nextLine()){
            case "1":{
                System.out.println("Enter folder path:");
                folderPath = scanner.nextLine();
            }
            case "2":{
                break;
            }
            default:
                System.exit(0);
        }

        System.out.println("Select connection type:\n1) With connection string.\n2) With details.");
        switch (scanner.nextLine()){
            case "1":
                ConnectionCheck connectionCheck = new ConnectionCheck();
                ping(connectionCheck.getHost(), connectionCheck.getTimeout());
                connect(connectionCheck.getTimeout(), folderPath, connectionCheck.getProvider(), connectionCheck.getConnectionString(), connectionCheck.getProperties());
                break;
            case "2":
                System.out.println("Select database type:\n1) DB2\n2) MSQL\n3) MySQL\n4) Oracle\n5) Snowflake\n6) PostgreSQL");

                switch (scanner.nextLine()){

                    case "1": {
                        dbConn = new DB2Connection(getConfigProperties("db2.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(), folderPath, Provider.DB2, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    case "2": {
                        dbConn = new MSQLConnection(getConfigProperties("msql.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(),folderPath, Provider.MICROSOFT, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    case "3": {
                        dbConn = new MySQLConnection(getConfigProperties("mysql.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(),folderPath, Provider.MYSQL, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    case "4": {
                        dbConn = new OracleConnection(getConfigProperties("oracle.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(),folderPath, Provider.ORACLE, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    case "5": {
                        new SnowflakeConnection(getConfigProperties("snowflake.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(),folderPath, Provider.SNOWFLAKE, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    case "6": {
                        dbConn = new PostgreSQLConnection(getConfigProperties("postgresql.properties"));
                        ping(dbConn.getHost(), dbConn.getTimeout());
                        connect(dbConn.getTimeout(),folderPath, Provider.POSTGRESQL, dbConn.getConnectionString(), dbConn.getProperties());
                        break;
                    }
                    default:{
                        System.exit(0);
                    }
                }
                break;
            default:
                System.exit(0);
        }
    }


    private static void ping(String hostName, int timeout){
        boolean isReachable = false;
        try {
            isReachable = InetAddress.getByName(hostName).isReachable(timeout * 1000);
        } catch (final UnknownHostException e) {
            LogSaver.appendLog(Level.WARNING, "InetAddress.getByName().isReachable(): got UnknownHostException");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isReachable) {
            System.out.println("Host is reachable");
            LogSaver.appendLog(Level.INFO, "Address is reachable");
        }
    }
    private static Properties getConfigProperties(String configName){
        String path = "src/resources/" + configName;
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(path));
        } catch (IOException e) {
            System.out.println("Failed to load config files");
        }
        return appProps;
    }

    private static void connect(int timeout, String path, Provider provider, String connectionString, Properties connectionProps){


        Driver driver;
        try {
            driver = DriverLoader.findDriver(path, provider);
        } catch (Exception e) {
            LogSaver.appendLog(Level.WARNING, e.getMessage());
            throw new RuntimeException(e);
        }

        final AtomicBoolean isConnected = new AtomicBoolean(false);
        Connection conn = null;
        try {
            new Thread(() -> {
                int counter = 0;
                while(counter < (timeout + 10) && !isConnected.get()){
                    counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!isConnected.get()){
                    System.out.println("Couldn't connect to DB");
                    LogSaver.appendLog(Level.WARNING, "Couldn't connect to DB");
                    System.exit(0);
                }

            }).start();
            conn = driver.connect(connectionString, connectionProps);
            isConnected.set(true);
        } catch (SQLException e) {
            if (e.getMessage().contains("Login failed for user")){
                LogSaver.appendLog(Level.WARNING, "Couldn't connect, wrong credentials!");
                System.out.println("Couldn't connect, wrong credentials!");
                System.exit(0);
            }
            LogSaver.appendLog(Level.WARNING, e.getMessage());
            System.out.println("error: " + e);
        }
        if (conn != null) {
            System.out.println("Connection through JDBC successfull!");
            LogSaver.appendLog(Level.FINE, "Connection through JDBC successfull!");
            try {
                conn.close();
            } catch (SQLException e) {
                LogSaver.appendLog(Level.WARNING, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
