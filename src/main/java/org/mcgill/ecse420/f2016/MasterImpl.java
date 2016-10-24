package org.mcgill.ecse420.f2016;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.mcgill.ecse420.f2016.Configs.MasterConfig;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;

public class MasterImpl implements Master {
  private WorkerPool workerPool;
  private MasterDb masterDb;

  public MasterImpl(int poolSize) throws DatabaseException {
    // Config DB for workers
    // Environment for workers
    EnvironmentConfig envConfigWorker = new EnvironmentConfig();
    envConfigWorker.setAllowCreate(true);
    // DB config for workers
    DatabaseConfig dbConfigWorker = new DatabaseConfig();
    dbConfigWorker.setAllowCreate(true);
    dbConfigWorker.setSortedDuplicates(false);
    // Set worker config
    WorkerConfig workerConfig =
        new WorkerConfig(envConfigWorker, dbConfigWorker);
    // Worker pool
    workerPool = new WorkerPool(poolSize, workerConfig, "default");

    // Config DB for Master
    // Environment for master
    EnvironmentConfig envConfigMaster = new EnvironmentConfig();
    envConfigMaster.setAllowCreate(true);
    // DB config for master
    DatabaseConfig dbConfigMaster = new DatabaseConfig();
    dbConfigMaster.setAllowCreate(true);
    dbConfigMaster.setSortedDuplicates(false);
    // Set master config
    MasterConfig masterConfig =
        new MasterConfig(envConfigMaster, dbConfigMaster);
    masterDb = new MasterDb(masterConfig);

  }

  @Override
  public String sayHello() throws RemoteException {
    return "Hell low";
  }

  public static void main(String args[]) {

    try {
      int poolsize = (args.length < 1) ? 1 : Integer.parseInt(args[0]);

      MasterImpl obj = new MasterImpl(poolsize);
      Master stub = (Master) UnicastRemoteObject.exportObject(obj, 0);

      // Bind the remote object's stub in the registry
      Registry registry = LocateRegistry.getRegistry();
      registry.bind("Master", stub);

      System.err.println("Server ready");
    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
