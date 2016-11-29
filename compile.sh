destDir=/tmp/berkeleydb/
mkdir -p $destDir
javac -d $destDir -cp ~/.m2/repository/com/sleepycat/je/3.3.75/je-3.3.75.jar src/main/java/org/mcgill/ecse420/f2016/Configs/*.java src/main/java/org/mcgill/ecse420/f2016/*.java
cp *sh $destDir
cp *jar $destDir
echo "Please go to $destDir and run the software using runServer.sh and runClient.sh"
