package connectionTool;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import connectionTool.cmd.ConfigArguments;
import connectionTool.cmd.DetailsArgument;
import connectionTool.cmd.HelpArgument;
import connectionTool.endpoints.DetailsProvider;
import connectionTool.endpoints.DB2Endpoint;
import connectionTool.endpoints.HanaDBEndpoint;
import connectionTool.endpoints.IConnection;
import connectionTool.endpoints.MSQLEndpoint;
import connectionTool.endpoints.MySQLEndpoint;
import connectionTool.endpoints.OracleEndpoint;
import connectionTool.endpoints.PostgreSQLEndpoint;
import connectionTool.endpoints.SnowflakeEndpoint;
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
import java.util.stream.Collectors;

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
            System.err.println(e.getMessage());
            LogSaver.appendLog(e.toString());
            System.exit(0);
        }
        String parsedCmdStr = jc.getParsedCommand();
        if (helpArgument.isHelp()){
            jc.usage();
            System.exit(0);
        }

        ConnectionMode connectionMode = getConnectionMode(parsedCmdStr);

        if (connectionMode == ConnectionMode.DETAILS){
            if (detailsArgument.getHelp()){
                jc.usage();
                System.exit(0);
            }
            DetailsProvider detailsProvider = new DetailsProvider(detailsArgument.getConnectionString(),
                    detailsArgument.getUsername(),
                    detailsArgument.getPassword(),
                    detailsArgument.getTimeout(),
                    detailsArgument.isSsl(),
                    detailsArgument.isTrustCertificates());
            IConnection iConn = detailsProvider.createEndpoint();
            ping(iConn.getHost(), iConn.getTimeoutInSeconds());
            connect(detailsArgument.getDriverPath(), iConn);
        }
        else if (connectionMode == ConnectionMode.CONFIG){
            if (configArguments.getHelp()){
                jc.usage();
                System.exit(0);
            }
            connect(configArguments.getConfigPath(), configArguments.getDriverPath());
        }
        else {
            System.out.println("Connection mode not specified");
        }
    }

    private static ConnectionMode getConnectionMode(String parsedCmdStr) {
        ConnectionMode connectionMode = null;
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
        return connectionMode;
    }

    private static void ping(String hostName, int timeout){
        boolean isReachable = false;
        try {
            isReachable = InetAddress.getByName(hostName).isReachable(timeout * 1000);
        } catch (final UnknownHostException e) {
            LogSaver.printAndSaveMessage(e.getMessage(), e.getStackTrace());
            System.exit(0);
        } catch (IOException e) {
            LogSaver.printAndSaveMessage("Couldn't ping host: " + e.getMessage(), e.getStackTrace());
            System.exit(0);
        }
        if (isReachable) {
            LogSaver.printAndSaveMessage("Host is reachable");
        }
        else  {
            LogSaver.printAndSaveMessage("Host is unreachable");
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
            LogSaver.printAndSaveMessage("Failed to load config files", "Failed to load config files: " + e);
            System.exit(0);
        }
        return props;
    }

    private static void connect(String path, IConnection connection){
        Driver driver = null;
        try {
            driver = DriverLoader.findDriver(path, connection.getProvider());
        } catch (DriverNotFoundException e) {
            LogSaver.printAndSaveMessage(e.getMessage(), e.getStackTrace());
            System.exit(0);
        }
        Connection conn = null;
        try {
            DriverManager.setLoginTimeout(connection.getTimeoutInSeconds());
            conn = driver.connect(connection.getConnectionString(), connection.getConnectionProperties());
        } catch (SQLException e) {
            LogSaver.printAndSaveMessage("Couldn't connect to database: " + e.getMessage(), e.getStackTrace());
            System.exit(0);
        }
        if (conn != null) {
            LogSaver.printAndSaveMessage("Connection through JDBC successfull!");
            try {
                conn.close();
            } catch (SQLException e) {
                LogSaver.printAndSaveMessage("Connection problem: " + e.getMessage(), e.getStackTrace());
                System.exit(0);
            }
        }
    }

    private static void connect(String configPath, String driverPath){

        String slashType = "\\";
        if (!System.getProperty("os.name").startsWith("Windows")){
            slashType = "/";
        }
        int folderIndex = configPath.lastIndexOf(slashType);
        String propertyName = configPath.substring(folderIndex + 1);
        IConnection dbConn = getConnectionType(configPath, propertyName);
        LogSaver.appendLog("Connection string: " + dbConn.getConnectionString());
        ping(dbConn.getHost(), dbConn.getTimeoutInSeconds());
        connect(driverPath, dbConn);
    }

    private static IConnection getConnectionType(String configPath, String propertyName) {
        IConnection dbConn = null;
        switch (propertyName){
            case "db2.properties":
                dbConn = new DB2Endpoint(getConfigProperties(configPath));
                break;
            case "hanadb.properties":
                dbConn = new HanaDBEndpoint(getConfigProperties(configPath));
                break;
            case "msql.properties":
                dbConn = new MSQLEndpoint(getConfigProperties(configPath));
                break;
            case "mysql.properties":
                dbConn = new MySQLEndpoint(getConfigProperties(configPath));
                break;
            case "oracle.properties":
                dbConn = new OracleEndpoint(getConfigProperties(configPath));
                break;
            case "postgresql.properties":
                dbConn = new PostgreSQLEndpoint(getConfigProperties(configPath));
                break;
            case "snowflake.properties":
                dbConn = new SnowflakeEndpoint(getConfigProperties(configPath));
                break;
            default:
                LogSaver.printAndSaveMessage("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
                System.exit(0);
        }
        return dbConn;
    }
}
