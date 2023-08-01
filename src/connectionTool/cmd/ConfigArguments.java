package connectionTool.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
        commandNames = { "config" }
)
public class ConfigArguments {
    @Parameter(names = {"-cp", "--config_path"},
            description = "provide path where the config properties are"
    )
    private String configPath;
    @Parameter(names = {"-dp", "--driver_path"},
            description = "provide path where the driver is"
    )
    private String driverPath;

    @Parameter(names = {"-h", "--help"}, help = true)
    private boolean help;

    public String getConfigPath() {
        return configPath;
    }

    public String getDriverPath() {
        return driverPath;
    }

    public boolean getHelp() {
        return help;
    }
}
