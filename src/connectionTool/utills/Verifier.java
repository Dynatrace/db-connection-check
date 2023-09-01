package connectionTool.utills;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Verifier {


    private Verifier(){}
    private static void verify(List<String> requiredArguments, List<String> missedArguments, Properties configProperties){
        for (String requiredArgument : requiredArguments){
            String argument = (String) configProperties.get(requiredArgument);
            if (argument == null || argument.isEmpty()){
                missedArguments.add(requiredArgument);
            }
        }
    }
    public static void verifyConfig(List<String> requiredArguments, Properties configProperties){
        List<String> missedArguments = new ArrayList<>();
        verify(requiredArguments, missedArguments, configProperties);
        if (!missedArguments.isEmpty()){
            for (String field : missedArguments) {
                System.out.println("Required field is empty: " + field);
            }
            System.exit(0);
        }
    }


}
