# DB Connection Check Tool

This is a quick tool to verify database connectivity, mainly used for
troubleshooting problems in customer environment.
<br>
# Building
We require that Java JDK 11 is available in the system. To build the tool simply execute the build script: build.sh on Linux or build.bat on Windows.
Windows
```
build.bat
```
Linux
```
./build.sh
```
*If missing execution privileges error occurs, enter this command first:
```
chmod a+x
```

# Usage
The recommended way of using the tool is to copy the db-connection-check directory with subdirectories to the machine where ActiveGate is installed and where the database datasource is running because that reflects the production environment including network and firewall settings as well as it should have all necessary drivers installed in the native directories:

Windows
```
C:\Program Files\dynatrace\remotepluginmodule\agent\res\java\libs
```
HanaDB and DB2 Windows
```
C:\ProgramData\dynatrace\remotepluginmodule\agent\conf\userdata\libs
```
Linux
```
/var/lib/dynatrace/remotepluginmodule/agent/res/java/libs
```
HanaDB and DB2 Linux
```
/var/lib/dynatrace/remotepluginmodule/agent/conf/userdata/libs
```

Otherwise, the drivers must be placed on the local machine (where the tool is executed) 
and the location needs to be specified as -dp arguments
# Testing connection

Tool has two modes, to see all available commands run command

Linux:
```
./run.sh -h
./run.sh --help
```

*If missing execution privileges error occurs, enter this command first:
```
chmod a+x
```
<br>
<br>

Windows:
```
run.bat -h
run.bat --help
```


1. Details mode,</br>
    &emsp; Usage: details [options]<br />
    &emsp;&emsp;Options:<br />
    &emsp;&emsp;&emsp;-cs, --connection_string<br />
    &emsp;&emsp;&emsp;&emsp;provide connection string, for example: jdbc:mysql://HOST/DATABASE<br />
    &emsp;&emsp;&emsp;-p, --password<br />
    &emsp;&emsp;&emsp;&emsp;password <br />
    &emsp;&emsp;&emsp;-t, --timeout<br />
    &emsp;&emsp;&emsp;&emsp;timeout<br />
    &emsp;&emsp;&emsp;&emsp;Default: 0<br />
    &emsp;&emsp;&emsp;-u, --username<br />
    &emsp;&emsp;&emsp;&emsp; username<br />
    &emsp;&emsp;&emsp;-dp, --driver_path [optional]<br />
    &emsp;&emsp;&emsp;&emsp;provide path to the folder with driver<br />
    &emsp;&emsp;&emsp;-h, --help [optional]<br /> 
    &emsp;&emsp;&emsp;&emsp; information about available commands and options<br />
    &emsp;&emsp;&emsp;-s, --ssl [optional]<br />
    &emsp;&emsp;&emsp;&emsp;should connection be encrypted<br />
    &emsp;&emsp;&emsp;-tc, --trust_certificates [optional]<br />
    &emsp;&emsp;&emsp;&emsp;should ssl trust server certificates [only for SQL Server]<br />


Usage:

Linux examples:
```
./run.sh details -cs jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30
./run.sh details -cs jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30 -dp "/usr/local/drivers"
```
Windows examples:
```
run.bat details -cs jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30
run.bat details -cs jdbc:db2://db2:25000/SAMPLE -u username -p password -t 30 -dp "C:\Program Files\drivers"
```
<br/>
2. Config file mode,<br />
    &emsp; Usage: details [options]<br />
    &emsp;&emsp;Options:<br />
    &emsp;&emsp;&emsp;-cp, --config_path<br />
    &emsp;&emsp;&emsp;&emsp;provide path to the config file<br />
    &emsp;&emsp;&emsp;-dp, --driver_path [optional]<br />
    &emsp;&emsp;&emsp;&emsp;provide path to the folder with driver<br />
    &emsp;&emsp;&emsp;-h, --helpConnection [optional]<br/>
    &emsp;&emsp;&emsp;&emsp; information about available commands and options 

Properties configs are stored in: <br />
```
 \connectionTool\resources\
```
(Do not rename .properties files)

Usage:

Linux examples:
```
./run.sh config -cp "/usr/connectionTool/resources/db2.properties"
./run.sh config -cp "/usr/connectionTool/resources/db2.properties" -dp "/usr/local/drivers"
```
Windows examples:
```
run.bat config -cp "C:\usrs\connectionTool\resources\db2.properties"
run.bat config -cp "C:\usrs\connectionTool\resources\mysql.properties" -dp "C:\Program Files\drivers"
```
# SSL
To configure SSL connection follow this instruction regarding installing server's certificates:
https://www.dynatrace.com/support/help/shortlink/oraclesql-monitoring#ssl
