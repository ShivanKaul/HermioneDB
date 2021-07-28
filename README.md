BerkeleyDDB
---------

Using [BerkeleyDB](https://en.wikipedia.org/wiki/Berkeley_DB) to make a distributed database. 

### Features:
- Consistent hashing to balance load across Workers
- Server pushed client-side look ups similar to Google BigTable and EdgeBase
- Table support
- Client side caching


### Running Instructions

Make sure you have access to _/tmp/_ directory of your machine.

To compile and run the software:
  1. Run *compile.sh* script. (Do it on each host if using multiple hosts)
  2. Go to the directory _/tmp/berkeleydb/_
  3. On the master host, run *runServer.sh* script.
  4. On each worker host, run *runWorker.sh \<Database_Name\> \<IP_of_Master\>*.
   - It is normal to see an exception about the already used port for rmi registry if more than one workers or the master is running on the same machine. However if the exception does not allow the program to start, please run *killrmis.sh* script and try the step 4 again.If this doesn't work either, run `ps aux | grep rmiregistry` and `kill -9` the process.
  5. On each client, run *runClient.sh \<IP_of_Master\>*.
  6. Have fun!
  7. To turn off the system, please use *Ctrl + c* on each host running one of the *runXX.sh* scripts. For killing rmi registries, please run *killrmis.sh* script. 



#### In progress:
- Transactions
- Replication
