package connection_tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class LogSaver {

    private static final String LOG_FILE_DIRECTORY = "log.txt";
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static void appendLog(Level lvl, String text)
    {
        File logFile = new File(LOG_FILE_DIRECTORY);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println("Couldn't create the log file");
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

            final String date = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT));

            buf.append(date + "\n" +
            lvl.toString() + ":\n" + text);
            buf.newLine();
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            System.out.println("Couldn't write to log.txt");
        }
    }
}
