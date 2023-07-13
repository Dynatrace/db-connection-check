#!/bin/bash

java -cp .libs;. -Dconfig=src/connectionTool/resources/config.properties connectionTool.Main  %*
