package connectionTool.constants;

import connectionTool.endpoints.DatabaseProvider;

public class DriverPathProvider {

    private static final String MAIN_DRIVER_PATH_WINDOWS = "C:\\Program Files\\dynatrace\\remotepluginmodule\\agent\\res\\java\\libs";
    private static final String MAIN_DRIVER_PATH_LINUX = "/opt/dynatrace/remotepluginmodule/agent/res/java/libs";
    private static final String OTHER_DRIVER_PATH_WINDOWS = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\libs";         //for DB2 and HANA
    private static final String OTHER_DRIVER_PATH_LINUX = "/var/lib/dynatrace/remotepluginmodule/agent/conf/userdata/libs";

    private DriverPathProvider() {
    }

    public static String provideDriverPath(DatabaseProvider provider){
        if (provider != DatabaseProvider.HANA_DB && provider != DatabaseProvider.DB2)
            return System.getProperty("os.name").startsWith("Windows")
                    ? MAIN_DRIVER_PATH_WINDOWS
                    : MAIN_DRIVER_PATH_LINUX;
        else return System.getProperty("os.name").startsWith("Windows")
                ? OTHER_DRIVER_PATH_WINDOWS
                : OTHER_DRIVER_PATH_LINUX;
    }
}
