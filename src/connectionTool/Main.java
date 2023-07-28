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
import connectionTool.exceptions.CommandLineArgumentException;
import connectionTool.exceptions.DriverNotFoundException;
import connectionTool.exceptions.ConfigUnknownException;
import connectionTool.utills.ConnectionMode;
import connectionTool.utills.DriverLoader;
import connectionTool.utills.LogSaver;
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
        args = new String[]{"-m","2","-cs","jdbc:db2://db2DateInstance.lab.dynatrace.org:25000/SAMPLE","-u","db2inst1","-p", "labpass","-t" ,"30"};
        LogSaver.appendLog("Arguments provided: " + Arrays.toString(args));

        ConnectionMode connectionMode;
        List<String> argList = Arrays.stream(args).collect(Collectors.toList());

        if (!argList.contains("-m")){
            throw new CommandLineArgumentException("Argument mode (-m) must be provided");
        }

        String modeArgValue = args[Arrays.stream(args).collect(Collectors.toList()).indexOf("-m") + 1];
        if (!modeArgValue.equals("1") && !modeArgValue.equals("2")){
            throw new CommandLineArgumentException("Argument mode (-m) must equal 1 or 2 and You have provided " + modeArgValue + ". Try changing it");
        }

        Options selectedOptions;

        if (modeArgValue.equals("1")){
            selectedOptions = getConfigOptions();
            connectionMode = ConnectionMode.CONFIG;
        }else {
            selectedOptions = getDetailsOptions();
            connectionMode = ConnectionMode.DETAILS;
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(selectedOptions, args);
        } catch (ParseException e) {
            System.out.println("Couldn't parse command line arguments");
            LogSaver.appendLog("Couldn't parse command line arguments: " + e);
            throw new RuntimeException(e);
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
            makeConnection(commandLine.getOptionValue("c"), folderPath);
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
            LogSaver.appendLog(e.getMessage());
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
            LogSaver.appendLog(e.getMessage());
            System.out.println("Couldn't connect, to database, check logs for details");
            System.exit(0);
        }
        if (conn != null) {
            System.out.println("Connection through JDBC successfull!");
            LogSaver.appendLog("Connection through JDBC successfull!");
            try {
                conn.close();
            } catch (SQLException e) {
                LogSaver.appendLog(e.getMessage());
                System.out.println("Connection problems, check logs for details");
                System.exit(0);
            }
        }
    }

    private static Options getDetailsOptions(){
        Option modeArg = Option.builder("m")
                .longOpt("mode")
                .hasArg()
                .required(true)
                .desc("provide connection mode:\n" +
                        "1 - with details\n" +
                        "2 - with config\n")
                .type(Integer.class)
                .valueSeparator('=')
                .build();
        Option connectionStringArg = Option.builder("cs")
                .longOpt("connection_string")
                .hasArg()
                .required(true)
                .desc("provide connection string, for example: jdbc:mysql://HOST/DATABASE")
                .type(String.class)
                .valueSeparator('=')
                .build();
        Option usernameArg = Option.builder("u")
                .longOpt("username")
                .hasArg()
                .required(true)
                .desc("provide username")
                .type(String.class)
                .valueSeparator('=')
                .build();
        Option passwordArg = Option.builder("p")
                .longOpt("password")
                .hasArg()
                .required(true)
                .desc("provide password")
                .type(String.class)
                .valueSeparator('=')
                .build();
        Option timeoutArg = Option.builder("t")
                .longOpt("timeout")
                .hasArg()
                .required(true)
                .desc("provide timeout")
                .type(Integer.class)
                .valueSeparator('=')
                .build();
        Option driverPathArg = Option.builder("dp")
                .longOpt("driver_path")
                .hasArg()
                .required(false)
                .desc("provide path to the folder where your drivers are stored")
                .type(String.class)
                .valueSeparator('=')
                .build();

        Options options = new Options();
        options.addOption(modeArg);
        options.addOption(connectionStringArg);
        options.addOption(usernameArg);
        options.addOption(passwordArg);
        options.addOption(timeoutArg);
        options.addOption(driverPathArg);

        return options;
    }

    private static Options getConfigOptions(){

        Option configArg = Option.builder("c")
                .longOpt("config")
                .hasArg()
                .required(true)
                .desc("provide path to the property file:\n" +
                        "db2.properties for DB2\n" +
                        "hanadb.properties for HANADB\n" +
                        "msql.properties for MSSQL\n" +
                        "mysql.properties for MySQL\n" +
                        "oracle.properties for OracleDB\n" +
                        "postgresql.properties for PostgreSQL\n" +
                        "snowflake.properties for SnowflakeDB\n")
                .type(String.class)
                .valueSeparator('=')
                .build();
        Option modeArg = Option.builder("m")
                .longOpt("mode")
                .hasArg()
                .required(true)
                .desc("provide connection mode:\n" +
                        "1 - with details\n" +
                        "2 - with config")
                .type(Integer.class)
                .valueSeparator('=')
                .build();
        Option driverPathArg = Option.builder("dp")
                .longOpt("driver_path")
                .hasArg()
                .required(true)
                .desc("provide path to the folder where your drivers are stored")
                .type(String.class)
                .valueSeparator('=')
                .build();
        Options options = new Options();

        options.addOption(configArg);
        options.addOption(driverPathArg);
        options.addOption(modeArg);
        return options;
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
                throw new ConfigUnknownException("Couldn't resolve config file: " + propertyName + " from path: " + configPath);
        }
    }
}
