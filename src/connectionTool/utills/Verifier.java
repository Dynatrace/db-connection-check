package connectionTool.utills;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Verifier {

    private static <T> void verify(List<String> requiredArguments, T t, String... args) throws NoSuchFieldException, IllegalAccessException {
        for (String arg : args) {
            Field field  = t.getClass().getDeclaredField(arg);
            field.setAccessible(true);
            String value = (String) field.get(t);
            if (value == null || value.isEmpty()){
                requiredArguments.add(arg);
            }
        }
    }
    public static <T> void verifyConfig(T t, String... args){
        List<String> requiredArguments = new ArrayList<>();
        try {
            verify(requiredArguments,t, args);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (!requiredArguments.isEmpty()){
            for (String field : requiredArguments) {
                System.out.println("Required field is empty: " + field);
            }
            System.exit(0);
        }
    }
}
