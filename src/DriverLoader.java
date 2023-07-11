import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public class DriverLoader {

    private final static String DRIVER_PATH_WINDOWS = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\libs\\";
    private final static String DRIVER_PATH_LINUX = "\\var\\lib\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\libs\\";
    private final static String HANA_DRIVER_PATH_WINDOWS = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\res\\userdata\\libs\\";
    private final static String HANA_DRIVER_PATH_LINUX = "\\var\\lib\\dynatrace\\remotepluginmodule\\agent\\res\\userdata\\libs\\";

    public static Driver findDriver(String folderPath, Provider db) throws Exception {
        File file;
        if (folderPath == null){
            file = new File(checkOs(db));
        }else {
            file = new File(folderPath);
        }

        Driver dr = Arrays.stream(file.listFiles())
                .filter(fl -> fl.getName().endsWith(".jar"))
                .map(fl -> {
                            URL url = null;
                            try {
                                url = fl.toURL();
                            } catch (MalformedURLException e) {
                                System.out.println("Wrong driver's path");
                            }
                            URL[] urls = new URL[]{url};

                            ClassLoader cl = new URLClassLoader(urls);
                            try {
                                return (Driver)Class.forName(getDriverClassName(db), true, cl).newInstance();
                            } catch (ClassNotFoundException ignored) {

                            } catch (InstantiationException e) {
                                System.out.println("Couldn't instantiate the driver's class");
                                LogSaver.appendLog(Level.WARNING,"Couldn't instantiate the driver's class");
                            } catch (IllegalAccessException e) {
                                System.out.println("Couldn't access the driver's class");
                                LogSaver.appendLog(Level.WARNING,"Couldn't instantiate the driver's class");
                            }
                            return null;
                        }
                ).filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new Exception("Couldn't find the driver"));

        LogSaver.appendLog(Level.INFO, "Driver found: " + db.name() + "\n" +
                "version: " + dr.getMajorVersion() + "." + dr.getMinorVersion() + "\n" +
                "driver path: " + file.getAbsolutePath());
        return dr;
    }


    private static String getDriverClassName(Provider provider){
        switch (provider){
            case MYSQL: return "org.mariadb.jdbc.Driver";
            case MICROSOFT: return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case ORACLE: return "oracle.jdbc.OracleDriver";
            case DB2: return "com.ibm.db2.jcc.DB2Driver";
            case HANA_DB: return "com.sap.db.jdbc.Driver";
            case POSTGRESQL: return "org.postgresql.Driver";
            default: return "";
        }
    }

    private static String checkOs(Provider db){
        if (db != Provider.HANA_DB)
            return System.getProperty("os.name").startsWith("Windows")
                    ? DRIVER_PATH_WINDOWS
                    : DRIVER_PATH_LINUX;
        else return System.getProperty("os.name").startsWith("Windows")
                ? HANA_DRIVER_PATH_WINDOWS
                : HANA_DRIVER_PATH_LINUX;
    }
}
