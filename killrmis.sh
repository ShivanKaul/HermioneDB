echo -e "Process running rmi registry: ";
ps | grep rmiregistry;

echo "** Killing rmiregistry process...";
kill `ps -a | grep rmi | cut -d ' ' -f 1`;

