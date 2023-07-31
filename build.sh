#!/bin/bash
cp -a src/connectionTool/resources/*.properties connectionTool/resources
javac src/connectionTool/connections/*.java src/connectionTool/*.java src/connectionTool/exceptions/*.java src/connectionTool/utills/*.java tests/ConnectionStringCreatingTest.java -d .