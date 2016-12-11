destDir=/tmp/berkeleydb/
mkdir -p $destDir
mkdir -p /tmp/master /tmp/worker
javac -d $destDir -cp .:\* src/main/java/org/mcgill/ecse420/f2016/Result/*.java src/main/java/org/mcgill/ecse420/f2016/Configs/*.java src/main/java/org/mcgill/ecse420/f2016/*.java
cp *sh $destDir
cp *jar $destDir
echo "Please go to $destDir and run the software using runServer.sh, runClient.sh and runWorker.sh"
