#!/bin/bash

javac -cp lib/*.jar src/connectionTool/connections/*.java src/connectionTool/*.java src/connectionTool/exceptions/*.java src/connectionTool/utills/*.java tests/ConnectionStringCreatingTest.java -d target