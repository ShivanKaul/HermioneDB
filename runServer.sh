rmiregistry&;
echo -e "Process running rmi registry: "
ps | grep rmiregistry

java -classpath je-3.3.75.jar:. -Djava.rmi.server.codebase=file:. org.mcgill.ecse420.f2016.MasterImpl $1
