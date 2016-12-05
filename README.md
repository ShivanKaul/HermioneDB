BerkeleyDDB
---------

Taking [BerkeleyDB](https://en.wikipedia.org/wiki/Berkeley_DB) and making it distributed.

Make sure you have access to /tmp/ directory of your machine.

To compile and run the software:
1. Run compile.sh script.(do it on each host if using multiple hosts)
2. Go to the directory '/tmp/berkeleydb/'
3. On the master host, run runServer.sh script.
4. On each worker host, run runWorker.sh <Database_Name> <IP_of_Master>.
4.1 It is normal to see an exception about the already used port for rmi registry if more than one workers or the master is running on the same machine. However if the exception does not allow the program to start, please run killrmis.sh script and try the step 4 again.
5. On each client, run runClient.sh <IP_of_Master>.
6. Have fun!
7. To turn off the system, please use Ctrl + c on each host running one of the runXX.sh scripts. For killing rmi registries, please run killrmis.sh script. 

Done:
- Minimum Viable Product
- Table support
- Client side caching
- Server pushed client-side look ups similar to Google BigTable and EdgeBase

In progress:
- Efficient hashing
- Transactions
- Replication
