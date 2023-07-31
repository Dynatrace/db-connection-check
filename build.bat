@echo off
xcopy /s src\connectionTool\resources connectionTool\resources\*.properties
javac -classpath "lib/commons-cli-1.5.0.jar" src\connectionTool\connections\*.java src\connectionTool\*.java src\connectionTool\exceptions\*.java src\connectionTool\utills\*.java -d .