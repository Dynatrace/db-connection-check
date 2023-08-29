package connectionTool.utills;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LogSaver {

	private static final String LOG_FILE_DIRECTORY = "log.txt";
	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

	private LogSaver(){}
	public static void appendLog(String text)
	{
		File logFile = new File(LOG_FILE_DIRECTORY);
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			System.err.println("Couldn't create log.txt file");
			throw new RuntimeException(e);
		}
		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

			final String date = LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

			buf.append(date)
					.append(" ")
					.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			System.out.println("Couldn't write to log.txt");
			System.exit(0);
		}
	}

	public static void printAndSaveMessage(String message, StackTraceElement[] stackTraceElements){
		printAndSaveMessage(message, Arrays.stream(stackTraceElements).map(Object::toString).collect(Collectors.joining("\n")));
	}
	public static void printAndSaveMessage(String... messages){
		if (messages.length > 2){
			System.err.println("You can't use more than 2 arguments!");
			System.exit(0);
		}
		if (messages.length == 2){
			System.out.println(messages[0]);
			LogSaver.appendLog(messages[1]);
		}
		if (messages.length == 1){
			System.out.println(messages[0]);
			LogSaver.appendLog(messages[0]);
		}
	}
}
