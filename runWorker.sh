#!/bin/bash

java -classpath je-3.3.75.jar:. -Djava.rmi.server.codebase=file:. org.mcgill.ecse420.f2016.WorkerImpl $1 $2;
