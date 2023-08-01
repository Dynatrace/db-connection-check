# DB Connection Check Tool

This is a quick tool to verify database connectivity, mainly used for
troubleshooting problems in customer environment.

# Building
We assume that Java JDK 11 is available in the system. To build the tool simply execute the build script: build.sh on Linux or build.bat on Windows.

# Usage
The recommended way of using the tool is to copy the connectionTool directory to the machine where ActiveGate is installed and where the database datasource is running because that reflects the production environment including network and firewall settings as well as it should have all necessary drivers installed in the native directories:

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

Otherwise the drivers must be placed on the local machine (where the tool is executed).

# Testing connection

Tool has two modes: 

Details mode (1),<br />
connection parameters: (-m) mode (-cs) connection string (-u) username (-p) password (-t) timeout (-dp) driver path [Optional].<br />
Usage examples:

Linux examples:
```
./run.sh -m 1 -cs  jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30
./run.sh -m 1 -cs  jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30 -dp /usr/local/drivers
```
Windows examples:
```
run.bat -m 1 -cs  jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30
run.bat -m 1 -cs  jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30 -dp C:\Program Files\drivers
```

Config file mode (2),<br />
connection parameters: (-m) mode (-cp) config path (-dp) driver path [Optional].<br />
Connection properties configs are stored in 
```
 \connectionTool\resources\
```

Usage examples:

Linux examples:
```
./run.sh -m 2 -cp
./run.sh -m 2 -cp  jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30 -dp /usr/local/drivers
```
Windows examples:
```
run.bat -m 2 -cp  C:\usrs\connectionTool\resources\db2.properties
run.bat -m 2 -cp  C:\usrs\connectionTool\resources\mysql.properties -dp C:\Program Files\drivers
```

