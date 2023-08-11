@echo off
xcopy /s src\connectionTool\resources connectionTool\resources\*.properties
javac -classpath "lib\jcommander-1.82.jar" src\connectionTool\constants\*.java src\connectionTool\cmd\*.java src\connectionTool\endpoints\*.java src\connectionTool\*.java src\connectionTool\exceptions\*.java src\connectionTool\utills\*.java -d .