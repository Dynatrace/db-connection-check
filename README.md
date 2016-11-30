# DB Connection Check Tool

This is a **quick & dirty** tool to verify database connectivity, mainly used for
troubleshooting problems in customer environment.

## Building

We assume that Java JDK is available in the system. To build the tool simply 
execute the build script: `build.sh` on Linux or `build.bat` on Windows.

## Downloading pre-built package

You can also download compiled tool at: https://github.com/Dynatrace/db-connection-check/releases

## Running the tool

Before running the tool make sure required libraries are available in the `lib`
directory. Due to licensing limitations we can't include all libraries with the 
package.

### Providing JDBC libraries

Libraries that allow connecting to Microsoft SQL Server (jTDS driver) and 
MySQL (MariaDB Driver) are included. Libraries that allow connecting to Oracle database
need to be downloaded separately and placed in the `lib` folder.

Oracle JDBC driver can be downloaded at: http://www.oracle.com/technetwork/database/features/jdbc/default-2280470.html

In case you need to use Microsoft SQL ServerJDBC driver, you can download it 
at: https://msdn.microsoft.com/library/mt484311.aspx or directly from: 
http://clojars.org/repo/com/microsoft/sqlserver/sqljdbc4/4.0/sqljdbc4-4.0.jar

Please note that it's usually not needed, as AppMon uses the jTDS driver.

### Testing connection

Simply use the run script providing required parameters in the following order:
`jdbcConnectionString`, `login`, `password`, `timeout`

Database driver type will be detected based on the JDBC connection string format.

Linux example:

    ./run.sh jdbc:oracle:thin:@orahost:1521:orcl scott tiger 30

Windows example:

    run.bat jdbc:jtds:sqlserver://sqlserverHost;instance=sqlexpress sa dynatrace 30
