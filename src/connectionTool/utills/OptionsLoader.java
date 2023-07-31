package connectionTool.utills;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class OptionsLoader {

    public static Options getDetailsOptions(){
        Option connectionStringArg = Option.builder("cs")
                .longOpt("connection_string")
                .hasArg()
                .required(true)
                .desc("provide connection string, for example: jdbc:mysql://HOST/DATABASE")
                .type(String.class)
                .build();
        Option usernameArg = Option.builder("u")
                .longOpt("username")
                .hasArg()
                .required(true)
                .desc("provide username")
                .type(String.class)
                .build();
        Option passwordArg = Option.builder("p")
                .longOpt("password")
                .hasArg()
                .required(true)
                .desc("provide password")
                .type(String.class)
                .build();
        Option timeoutArg = Option.builder("t")
                .longOpt("timeout")
                .hasArg()
                .required(true)
                .desc("provide timeout")
                .type(Integer.class)
                .build();

        Options options = new Options();

        options.addOption(connectionStringArg);
        options.addOption(usernameArg);
        options.addOption(passwordArg);
        options.addOption(timeoutArg);
        addDefaultOptions(options);

        return options;
    }

    public static Options getConfigOptions(){

        Option configArg = Option.builder("cp")
                .longOpt("config path")
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
                .build();

        Options options = new Options();

        options.addOption(configArg);
        addDefaultOptions(options);

        return options;
    }


    private static void addDefaultOptions(Options options){
        Option modeArg = Option.builder("m")
                .longOpt("mode")
                .hasArg()
                .required(true)
                .desc("provide connection mode:\n" +
                        "1 - with details\n" +
                        "2 - with config")
                .type(Integer.class)
                .build();
        Option driverPathArg = Option.builder("dp")
                .longOpt("driver_path")
                .hasArg()
                .required(false)
                .desc("provide path to the folder where your drivers are stored")
                .type(String.class)
                .build();

        options.addOption(modeArg);
        options.addOption(driverPathArg);
    }
}
