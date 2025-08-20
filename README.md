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
HanaDB Windows
```
C:\ProgramData\dynatrace\remotepluginmodule\agent\conf\userdata\libs
```
Linux
```
/opt/dynatrace/remotepluginmodule/agent/res/java/libs
```
HanaDB Linux
```
/var/lib/dynatrace/remotepluginmodule/agent/conf/userdata/libs
```

Otherwise, the drivers must be placed on the local machine (where the tool is executed) 
and the location needs to be specified as -dp arguments

## DB2 Drivers
When testing connection with the default ActiveGate configuration, the DB2 drivers changed behaviour between ActiveGate versions 1.254 and 1.255, with the prior requiring the driver to be provided (same as HanaDB) and the latter using an already provided driver, like the rest of DB providers. The current version of this tool copies the behaviour of ActiveGate 1.255 and newer. If you however need to test older versions, simply use the code of this tool from the commit 107815f9ead0dc9063d0b5ac5c5a6473a0cc1dbd.
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
    &emsp;&emsp;&emsp;&emsp;add this flag to enable encrypted connection<br />
    &emsp;&emsp;&emsp;-tc, --trust_certificates [optional]<br />
    &emsp;&emsp;&emsp;&emsp;add this flag to trust server certificates [only for MS SQL]<br />


Usage:

Linux examples:
```
./run.sh details -cs jdbc:sap://hanadb:25000/SAMPLE -u username -p password -t 30
./run.sh details -cs jdbc:sap://hanadb:25000/SAMPLE -u username -p password -t 30 -dp "/usr/local/drivers"
```
Windows examples:
```
run.bat details -cs jdbc:sap://hanadb:25000/SAMPLE -u username -p password -t 30
run.bat details -cs jdbc:sap://hanadb:25000/SAMPLE -u username -p password -t 30 -dp "C:\Program Files\drivers"
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
 resources/
```
(Do not rename .properties files)

Usage:

Linux examples:
```
./run.sh config -cp "/usr/connectionTool/resources/hanadb.properties"
./run.sh config -cp "/usr/connectionTool/resources/hanadb.properties" -dp "/usr/local/drivers"
```
Windows examples:
```
run.bat config -cp "C:\usrs\connectionTool\resources\hanadb.properties"
run.bat config -cp "C:\usrs\connectionTool\resources\hanadb.properties" -dp "C:\Program Files\drivers"
```
# SSL
To configure SSL connection follow this instruction regarding installing server's certificates:
 https://www.dynatrace.com/support/help/extend-dynatrace/extensions20/data-sources/sql
 in the menu on the left You can find your vendor 
