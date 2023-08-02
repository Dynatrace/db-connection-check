package connectionTool.cmd;

import com.beust.jcommander.Parameter;

public class HelpArgument {

    @Parameter(names = {"-h", "--help"}, help = true)
    private boolean help;


    public boolean isHelp() {
        return help;
    }
}
