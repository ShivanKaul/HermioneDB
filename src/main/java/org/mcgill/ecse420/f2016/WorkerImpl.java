package org.mcgill.ecse420.f2016;

import com.sleepycat.je.*;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class WorkerImpl implements Worker {

    private Database workerDb;
    private String namePrefix = "worker_";

    // Constructor
    public WorkerImpl(WorkerConfig config,
                      String name) throws DatabaseException {
        workerDb = config.environment.openDatabase(null, namePrefix + name, config.dbConfig);
    }

    public static void main(String args[]) {
        // Config DB for workers
        // Environment for workers
        EnvironmentConfig envConfigWorker = new EnvironmentConfig();
        envConfigWorker.setAllowCreate(true);
        // DB config for workers
        DatabaseConfig dbConfigWorker = new DatabaseConfig();
        dbConfigWorker.setAllowCreate(true);
        dbConfigWorker.setSortedDuplicates(false);

        try {
            // Set worker config
            WorkerConfig workerConfig = new WorkerConfig(envConfigWorker, dbConfigWorker);
            WorkerImpl obj = new WorkerImpl(workerConfig, "test");
            Worker stub = (Worker) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);

            System.err.println("Worker ready");
        } catch (RemoteException e) {
            System.err.println("Remote exception: " + e.toString());
            e.printStackTrace();
        }
        catch (AlreadyBoundException e) {
            System.err.println("Problem while binding: " + e.toString());
            e.printStackTrace();
        }
        catch (DatabaseException e) {
            System.out.println(String.format
                    ("Encountered database exception while trying to initialize worker config: %s", e.toString()));
            e.printStackTrace();

        }
    }

    // Remote Method: Will be called by client driver after getting reference to this worker
    public Result get(DatabaseEntry key) throws DatabaseException {
        DatabaseEntry gotValue = new DatabaseEntry();
        OperationStatus opStatus = workerDb.get(null, key, gotValue, LockMode.DEFAULT); // Might throw database exception
        return new Result(opStatus, Result.WorkerPoolStatus.SUCCESS, gotValue);
    }
    // Remote Method: Will be called by client driver after getting reference to this worker
    public Result set(DatabaseEntry key, DatabaseEntry value) throws DatabaseException {
        OperationStatus opStatus = workerDb.put(null, key, value); // Might throw database exception
        return new Result(opStatus, Result.WorkerPoolStatus.SUCCESS, null);
    }
}
