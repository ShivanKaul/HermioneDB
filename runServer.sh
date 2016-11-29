#!/bin/bash

# trap ctrl-c and call ctrl_c()
trap ctrl_c INT

ctrl_c() {
        echo "** Trapped CTRL-C.. Killing rmiregistry process..."
        kill `ps -a | grep rmi | cut -d ' ' -f 1`
    }
rmiregistry &
echo -e "Process running rmi registry: ";
ps | grep rmiregistry;

java -classpath je-3.3.75.jar:. -Djava.rmi.server.codebase=file:. org.mcgill.ecse420.f2016.MasterImpl;
