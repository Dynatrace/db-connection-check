#!/bin/bash
cp -a src/connectionTool/resources/*.properties connectionTool/resources
javac -classpath "lib/jcommander-1.82.jar" src/connectionTool/cmd/*.java src/connectionTool/connections/*.java src/connectionTool/*.java src/connectionTool/exceptions/*.java src/connectionTool/utills/*.java tests/ConnectionStringCreatingTest.java -d .