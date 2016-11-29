package org.mcgill.ecse420.f2016;

import com.sleepycat.je.*;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;

import java.io.UnsupportedEncodingException;
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
        String name = (args.length < 1) ? null : args[0];
        String host = (args.length < 2) ? null : args[1];

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
            WorkerConfig workerConfig = new WorkerConfig(name.toLowerCase(), envConfigWorker, dbConfigWorker);
            WorkerImpl obj = new WorkerImpl(workerConfig, name.toLowerCase());
            Worker stub = (Worker) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(host);
            registry.bind(name.toLowerCase(), stub);

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
    public Result get(String key) throws DatabaseException, UnsupportedEncodingException {
        DatabaseEntry gotValue = new DatabaseEntry();
        OperationStatus opStatus = workerDb.get(null, new DatabaseEntry(key.getBytes("UTF-8")), gotValue, LockMode.DEFAULT); // Might throw database exception
        boolean opStatusb = false;
        if (opStatus.SUCCESS == opStatus) opStatusb = true;
        try {
            return new Result(opStatusb, true, new String(gotValue.getData(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(opStatusb, false, null);
        }
    }
    // Remote Method: Will be called by client driver after getting reference to this worker
    public Result set(String key, String value) throws DatabaseException, UnsupportedEncodingException {
        OperationStatus opStatus = workerDb.put(null, new DatabaseEntry(key.getBytes("UTF-8")), new DatabaseEntry(value.getBytes("UTF-8"))); // Might throw database exception
        boolean opStatusb = false;
        if (opStatus.SUCCESS == opStatus) opStatusb = true;
        return new Result(opStatusb, true, null);
    }
}
