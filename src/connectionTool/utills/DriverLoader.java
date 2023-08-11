package connectionTool.utills;

import connectionTool.endpoints.DatabaseProvider;
import connectionTool.constants.DriverPathProvider;
import connectionTool.exceptions.DriverNotFoundException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class DriverLoader {

    private DriverLoader(){

    }

    public static Driver findDriver(String folderPath, DatabaseProvider db) throws DriverNotFoundException {
        String path = getPath(folderPath, db);
        File driverFolder = getDriverFolder(path);
        return Arrays.stream(Objects.requireNonNull(driverFolder.listFiles()))
                .map(mapFileToDriver(db))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DriverNotFoundException("Couldn't find the driver in: " + path));
    }
    private static String getDriverClassName(DatabaseProvider provider){
        switch (provider){
            case MYSQL: return "org.mariadb.jdbc.Driver";
            case MICROSOFT: return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case ORACLE: return "oracle.jdbc.OracleDriver";
            case DB2: return "com.ibm.db2.jcc.DB2Driver";
            case HANA_DB: return "com.sap.db.jdbc.Driver";
            case POSTGRESQL: return "org.postgresql.Driver";
            case SNOWFLAKE: return "com.snowflake.client.jdbc.SnowflakeDriver";
            default: return "";
        }
    }

    private static String getPath(String folderPath, DatabaseProvider db){
        String originalPath;
        if (folderPath == null){
            originalPath = DriverPathProvider.provideDriverPath(db);
        }else {
            originalPath = folderPath;
        }
        return originalPath;
    }
    private static File getDriverFolder(String path){
        File file = new File(path);
        if (file.listFiles() == null){
            errorCall("Wrong driver path: " + path);
        }
        if (Objects.requireNonNull(file.listFiles()).length == 0){
            errorCall("Path: "+ path + " is empty");
        }
        if (Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .noneMatch(fl -> fl.getName().endsWith(".jar"))){
            errorCall("No jars in path: "+ path);
        }
        return file;
    }

    private static void errorCall(String message){
        LogSaver.printAndSaveMessage(message);
        System.exit(0);
    }

    private static Function<File, Driver> mapFileToDriver(DatabaseProvider db){
        return file -> {
            URL url = null;
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException e) {
                System.out.println("Wrong driver's path");
                System.exit(0);
            }
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);
            try {
                Driver driver = (Driver)Class.forName(getDriverClassName(db), true, cl).getConstructor().newInstance();
                LogSaver.appendLog("Driver found: " + db.name() + " " +
                        "version: " + driver.getMajorVersion() + "." + driver.getMinorVersion() +
                        " driver path: " + file.getAbsolutePath());
                return driver;
            } catch (ClassNotFoundException ignored) {
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                LogSaver.printAndSaveMessage("Couldn't load the driver", e.toString());
                System.exit(0);
            }
            return null;
        };
    }
}
