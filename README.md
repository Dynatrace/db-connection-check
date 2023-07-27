# DB Connection Check Tool

This is a **quick & dirty** tool to verify database connectivity, mainly used for
troubleshooting problems in customer environment.

# Building
We assume that Java JDK is available in the system. To build the tool simply execute the build script: build.sh on Linux or build.bat on Windows.

# Drivers
If drivers aren't in the native directory
Windows
```
C:\ProgramFiles\dynatrace\remotepluginmodule\agent\res\java\libs\
```
HanaDB and DB2 Windows
```
C:\ProgramData\dynatrace\remotepluginmodule\agent\res\userdata\libs\
```
Linux
```
\var\lib\dynatrace\remotepluginmodule\agent\res\java\libs\
```
HanaDB and DB2 Linux
```
\var\lib\dynatrace\remotepluginmodule\agent\res\userdata\libs\
```

Make sure to to provide the path where drivers are stored when asked.

# Testing connection

Tool has two types of testing

With connection string.
User must provide connection_string username password timeout.
Database driver type will be detected based on the JDBC connection string format.

With config files
User must fill the src/connectionTool/resources/config.properties file with necessary data (example configs are stored in the same folder).

Linux example:
```
./run.sh
```
Windows example:
```
run.bat
```
