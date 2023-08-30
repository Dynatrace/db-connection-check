package connectionTool.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
        commandNames = { "details" }
)
public class DetailsArgument {
    @Parameter(names = {"-cs", "--connection_string"},
            description = "provide connection string, for example: jdbc:mysql://HOST/DATABASE",
            required = true
    )
    private String connectionString;

    @Parameter(names = {"-u", "--username"},
            description = "username",
            required = true
    )
    private String username;
    @Parameter(names = {"-p", "--password"},
            description = "password",
            required = true
    )
    private String password;
    @Parameter(names = {"-t", "--timeout"},
            description = "timeout",
            required = true
    )
    private int timeout;

    @Parameter(names = {"-dp", "--driver_path"},
            description = "provide path where the driver is"
    )
    private String driverPath;
    @Parameter(names = {"-h", "--help"}, help = true,
            description = "information about available commands and options")
    private boolean help;

    @Parameter(names = {"-s", "--ssl"},
            description = "Add this flag enable encrypted connection")
    private boolean ssl = false;

    @Parameter(names = {"-tc", "--trust_certificates"},
            description = "Add this flag to trust server certificates [only for MS SQL]")
    private boolean trustCertificates = false;

    public boolean isSsl(){
        return ssl;
    }

    public boolean isTrustCertificates() {
        return trustCertificates;
    }
    public String getConnectionString() {
        return connectionString;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getDriverPath() {
        return driverPath;
    }

    public boolean getHelp() {
        return help;
    }
}
