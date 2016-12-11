#!/bin/bash
trap ctrl_c INT

#ctrl_c() {
            #echo "** Trapped CTRL-C.. Killing rmiregistry process..."
                    ##kill `ps -a | grep rmi | cut -d ' ' -f 1`
                    #kill `ps | grep [r]miregistry | awk '{print $1}'`
        #}

mkdir -p /tmp/worker_$1
rmiregistry &
java -classpath .:\* -Djava.rmi.server.codebase=file:. org.mcgill.ecse420.f2016.WorkerImpl $1 $2;
