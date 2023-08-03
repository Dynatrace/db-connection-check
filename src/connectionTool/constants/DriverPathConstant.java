package connectionTool.constants;

import connectionTool.connections.Provider;

public class DriverPathConstant {

    private static final String MAIN_DRIVER_PATH_WINDOWS = "C:\\Program Files\\dynatrace\\remotepluginmodule\\agent\\res\\java\\libs";
    private static final String MAIN_DRIVER_PATH_LINUX = "\\opt\\dynatrace\\remotepluginmodule\\agent\\res\\java\\libs";
    private static final String OTHER_DRIVER_PATH_WINDOWS = "C:\\ProgramData\\dynatrace\\remotepluginmodule\\agent\\res\\userdata\\libs";         //for DB2 and HANA
    private static final String OTHER_DRIVER_PATH_LINUX = "\\var\\lib\\dynatrace\\remotepluginmodule\\agent\\res\\userdata\\libs";


    public static String provideDriverPath(Provider provider){
        if (provider != Provider.HANA_DB && provider != Provider.DB2)
            return System.getProperty("os.name").startsWith("Windows")
                    ? MAIN_DRIVER_PATH_WINDOWS
                    : MAIN_DRIVER_PATH_LINUX;
        else return System.getProperty("os.name").startsWith("Windows")
                ? OTHER_DRIVER_PATH_WINDOWS
                : OTHER_DRIVER_PATH_LINUX;
    }
}
