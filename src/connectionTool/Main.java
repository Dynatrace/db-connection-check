package connectionTool;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import connectionTool.cmd.ConfigArguments;
import connectionTool.cmd.DetailsArgument;
import connectionTool.cmd.HelpArgument;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        run(args);
    }

    private static void run(String[] args){
        LogSaver.appendLog("Arguments provided: " + Arrays.toString(args));
        HelpArgument helpArgument = new HelpArgument();
        DetailsArgument detailsArgument = new DetailsArgument();
        ConfigArguments configArguments = new ConfigArguments();
        JCommander jc = JCommander.newBuilder()
                .addObject(helpArgument)
                .addCommand(detailsArgument)
                .addCommand(configArguments)
                .build();
        if (args.length == 0){
            jc.usage();
            System.exit(0);
        }
        try {
            jc.parse(args);
        }catch (ParameterException e){
            System.out.println(e.getMessage());
            LogSaver.appendLog(e.toString());
            System.exit(0);
        }
        String parsedCmdStr = jc.getParsedCommand();
        ConnectionMode connectionMode = null;
        if (helpArgument.isHelp()){
            jc.usage();
            System.exit(0);
        }
        switch (parsedCmdStr) {
            case "details":
                connectionMode = ConnectionMode.DETAILS;
                break;

            case "config":
                connectionMode = ConnectionMode.CONFIG;
                break;

            default:
                System.err.println("Invalid command: " + parsedCmdStr);
                System.exit(0);
        }
        if (connectionMode == ConnectionMode.DETAILS){
            if (detailsArgument.getHelp()){
                jc.usage();
                System.exit(0);
            }
            ConnectionCheck connectionCheck = new ConnectionCheck(detailsArgument.getConnectionString(),
                    detailsArgument.getUsername(),
                    detailsArgument.getPassword(),
                    detailsArgument.getTimeout(),
                    detailsArgument.isSsl(),
                    detailsArgument.isTrustCertificates());
            ping(connectionCheck.getHost(), connectionCheck.getTimeout());
            connect(connectionCheck.getTimeout(), detailsArgument.getDriverPath(),
                    connectionCheck.getProvider(),
                    connectionCheck.getConnectionString(),
                    connectionCheck.getProperties());
        }
        else {
            if (configArguments.getHelp()){
                jc.usage();
                System.exit(0);
            }
            makeConnection(configArguments.getConfigPath(), configArguments.getDriverPath());
        }
    }
    private static void ping(String hostName, int timeout){
        boolean isReachable = false;
        try {
            isReachable = InetAddress.getByName(hostName).isReachable(timeout * 1000);
        } catch (final UnknownHostException e) {
            LogSaver.appendLog(e.toString());
            System.out.println("Host is unknown");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Couldn't ping host, check logs for details!");
            LogSaver.appendLog(e.toString());
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
            LogSaver.appendLog("Failed to load config files: " + e);
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
            LogSaver.appendLog(e.toString());
            System.exit(0);
        }
        Connection conn = null;
        try {
            DriverManager.setLoginTimeout(timeout);
            conn = driver.connect(connectionString, connectionProps);
        } catch (Exception e) {
            LogSaver.appendLog("Couldn't connect to database: " + e);
            System.out.println("Couldn't connect to database");
            System.exit(0);
        }
        if (conn != null) {
            System.out.println("Connection through JDBC successfull!");
            LogSaver.appendLog("Connection through JDBC successfull!");
            try {
                conn.close();
            } catch (SQLException e) {
                LogSaver.appendLog("Connection problems: " + e);
                System.out.println("Connection problems, check logs for details");
                System.exit(0);
            }
        }
    }

    private static void makeConnection(String configPath, String driverPath){
        IConnection dbConn = null;
        String slashType = "\\";
        if (!System.getProperty("os.name").startsWith("Windows")){
            slashType = "/";
        }
        int folderIndex = configPath.lastIndexOf(slashType);
        String propertyName = configPath.substring(folderIndex + 1);
        switch (propertyName){
            case "db2.properties":
                dbConn = new DB2Connection(getConfigProperties(configPath));
                break;
            case "hanadb.properties":
                dbConn = new HanaDBConnection(getConfigProperties(configPath));
                break;
            case "msql.properties":
                dbConn = new MSQLConnection(getConfigProperties(configPath));
                break;
            case "mysql.properties":
                dbConn = new MySQLConnection(getConfigProperties(configPath));
                break;
            case "oracle.properties":
                dbConn = new OracleConnection(getConfigProperties(configPath));
                break;
            case "postgresql.properties":
                dbConn = new PostgreSQLConnection(getConfigProperties(configPath));
                break;
            case "snowflake.properties":
                dbConn = new SnowflakeConnection(getConfigProperties(configPath));
                break;
            default:
                System.out.println("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
                LogSaver.appendLog("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
                System.exit(0);
        }
        LogSaver.appendLog("Connection string: " + dbConn.getConnectionString());
        ping(dbConn.getHost(), dbConn.getTimeout());
        connect(dbConn.getTimeout(), driverPath, dbConn.getProvider(), dbConn.getConnectionString(), dbConn.getProperties());
    }
}
