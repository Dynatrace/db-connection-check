package connectionTool;


import connectionTool.connections.ConnectionCheck;
import connectionTool.connections.DB2Connection;
import connectionTool.connections.HanaDBConnection;
import connectionTool.connections.IConnection;
import connectionTool.connections.MSQLConnection;
import connectionTool.connections.MySQLConnection;
import connectionTool.connections.OracleConnection;
import connectionTool.connections.PostgreSQLConnection;
import connectionTool.connections.Provider;
import connectionTool.connections.SnowflakeConnection;
import connectionTool.exceptions.DriverNotFoundException;
import connectionTool.utills.ConnectionMode;
import connectionTool.utills.DriverLoader;
import connectionTool.utills.LogSaver;
import connectionTool.utills.OptionsLoader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        run(args);
    }

    private static void run(String[] args){
        LogSaver.appendLog("Arguments provided: " + Arrays.toString(args));

        ConnectionMode connectionMode;
        List<String> argList = Arrays.stream(args).collect(Collectors.toList());

        if (!argList.contains("-m")){
            System.out.println("Argument mode (-m) must be provided");
            System.exit(0);
        }

        String modeArgValue = args[Arrays.stream(args).collect(Collectors.toList()).indexOf("-m") + 1];
        if (!modeArgValue.equals("1") && !modeArgValue.equals("2")){
            System.out.println("Argument mode (-m) must equal 1 or 2 and You have provided " + modeArgValue + ". Try changing it");
            System.exit(0);
        }

        Options selectedOptions;

        if (modeArgValue.equals("1")){
            selectedOptions = OptionsLoader.getDetailsOptions();
            connectionMode = ConnectionMode.DETAILS;
        }else {
            selectedOptions = OptionsLoader.getConfigOptions();
            connectionMode = ConnectionMode.CONFIG;
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(selectedOptions, args);
        } catch (ParseException e) {
            System.out.println("Couldn't parse command line arguments");
            LogSaver.appendLog("Couldn't parse command line arguments: " + e);
            System.exit(0);
        }

        String folderPath = commandLine.getOptionValue("dp");

        if (connectionMode == ConnectionMode.DETAILS){
            ConnectionCheck connectionCheck = new ConnectionCheck(commandLine.getOptionValue("cs"),
                    commandLine.getOptionValue("u"),
                    commandLine.getOptionValue("p"), Integer.parseInt(commandLine.getOptionValue("t")));
            ping(connectionCheck.getHost(), connectionCheck.getTimeout());
            connect(connectionCheck.getTimeout(), folderPath, connectionCheck.getProvider(), connectionCheck.getConnectionString(), connectionCheck.getProperties());
        }
        if (connectionMode == ConnectionMode.CONFIG){
            makeConnection(commandLine.getOptionValue("cp"), folderPath);
        }
    }
    private static void ping(String hostName, int timeout){
        boolean isReachable = false;
        try {
            isReachable = InetAddress.getByName(hostName).isReachable(timeout * 1000);
        } catch (final UnknownHostException e) {
            LogSaver.appendLog(e.getMessage());
            System.out.println("Host is unknown");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Couldn't ping host, check logs for details!");
            LogSaver.appendLog(e.getMessage());
            System.exit(0);
        }
        if (isReachable) {
            System.out.println("Host is reachable");
            LogSaver.appendLog("Host is reachable");
        }
        else  {
            System.out.println("Host is unreachable");
            LogSaver.appendLog("Host is unreachable");
            System.exit(0);
        }
    }
    private static Properties getConfigProperties(String path){
        Properties props = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(path);
            props.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.out.println("Failed to load config files");
            LogSaver.appendLog("Failed to load config files: " + e.getMessage());
            System.exit(0);
        }
        return props;
    }

    private static void connect(int timeout, String path, Provider provider, String connectionString, Properties connectionProps){
        Driver driver = null;
        try {
            driver = DriverLoader.findDriver(path, provider);
        } catch (DriverNotFoundException e) {
            System.out.println("Couldn't load the driver");
            LogSaver.appendLog(e.getMessage());
            System.exit(0);
        }
        Connection conn = null;
        try {
            driver.connect(connectionString, connectionProps)
                    .setNetworkTimeout(Executors.newFixedThreadPool(1), timeout * 1000);
            conn = driver.connect(connectionString, connectionProps);

        } catch (Exception e) {
            LogSaver.appendLog("Couldn't connect, to database: " + e.getMessage());
            System.out.println("Couldn't connect, to database");
            System.exit(0);
        }
        if (conn != null) {
            System.out.println("Connection through JDBC successfull!");
            LogSaver.appendLog("Connection through JDBC successfull!");
            try {
                conn.close();
            } catch (SQLException e) {
                LogSaver.appendLog("Connection problems: " + e.getMessage());
                System.out.println("Connection problems, check logs for details");
                System.exit(0);
            }
        }
    }

    private static void makeConnection(String configPath, String driverPath){
        IConnection dbConn;
        String slashType = "\\";
        if (!System.getProperty("os.name").startsWith("Windows")){
            slashType = "/";
        }
        int folderIndex = configPath.lastIndexOf(slashType);
        String propertyName = configPath.substring(folderIndex + 1);
        switch (propertyName){
            case "db2.properties":
                dbConn = new DB2Connection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(), driverPath, Provider.DB2, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "hanadb.properties":
                dbConn = new HanaDBConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(),driverPath, Provider.HANA_DB, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "msql.properties":
                dbConn = new MSQLConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(),driverPath, Provider.MICROSOFT, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "mysql.properties":
                dbConn = new MySQLConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(),driverPath, Provider.MYSQL, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "oracle.properties":
                dbConn = new OracleConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(), driverPath, Provider.ORACLE, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "postgresql.properties":
                dbConn = new PostgreSQLConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(),driverPath, Provider.POSTGRESQL, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            case "snowflake.properties":
                dbConn = new SnowflakeConnection(getConfigProperties(configPath));
                ping(dbConn.getHost(), dbConn.getTimeout());
                connect(dbConn.getTimeout(),driverPath, Provider.SNOWFLAKE, dbConn.getConnectionString(), dbConn.getProperties());
                break;
            default:
                System.out.println("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
                LogSaver.appendLog("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
                System.exit(0);
        }
    }
}
