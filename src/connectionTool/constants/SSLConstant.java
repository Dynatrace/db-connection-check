package connectionTool.constants;

public class SSLConstant {


    private static final String SSL_TRUSTSTORE_PATH_WINDOWS = "%PROGRAMDATA%\\dynatrace\\remotepluginmodule\\agent\\conf\\userdata\\sqlds_truststore";
    private static final String SSL_TRUSTSTORE_PATH_LINUX = "/var/lib/dynatrace/remotepluginmodule/agent/conf/userdata/sqlds_truststore";
    public static final String SSL_TRUSTSTORE_PASSWORD = "sqlds_truststore";

    public static String getSSLTrustStorePath(){

        return System.getProperty("os.name").startsWith("Windows")
                    ? SSL_TRUSTSTORE_PATH_WINDOWS
                    : SSL_TRUSTSTORE_PATH_LINUX;

    }

}
