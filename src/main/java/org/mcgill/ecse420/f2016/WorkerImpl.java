package org.mcgill.ecse420.f2016;

import com.sleepycat.je.*;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;
import org.mcgill.ecse420.f2016.Result.WorkerResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import static java.lang.System.err;
import static org.mcgill.ecse420.f2016.MasterImpl.RING_SIZE;

public class WorkerImpl implements Worker {

    private Database workerDb;
    private static String name;
    private static String masterIpAddress;

    // Constructor
    public WorkerImpl(WorkerConfig config,
                      String uniqueWorkerName) throws DatabaseException {
        workerDb = config.environment.openDatabase(null, uniqueWorkerName, config.dbConfig);
    }

    public static void main(String args[]) {
        name = (args.length < 1) ? null : args[0].toLowerCase();
        masterIpAddress = (args.length < 2) ? "localhost" : args[1];
        if (name == null) {
            System.out.println("Worker needs two arguments - table and master's IP address");
            System.exit(1);
        }

        int id = Math.abs(UUID.randomUUID().toString().hashCode()) % RING_SIZE;

        String uniqueWorkerName = String.format("Worker_%s_%d", name, id);

        File dir = new File(String.format("/tmp/%s", uniqueWorkerName));
        // attempt to create the directory here
        if (!dir.mkdir()) {
            System.out.println(String.format("Failed trying to create directory /tmp/%s, " +
                    "please make sure you have access, " +
                    "BerkeleyDDB cannot function without creating these directories"));
            System.exit(1);
        }

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
            WorkerConfig workerConfig = new WorkerConfig(uniqueWorkerName, envConfigWorker, dbConfigWorker);
            WorkerImpl obj = new WorkerImpl(workerConfig, uniqueWorkerName);
            Worker stub = (Worker) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(uniqueWorkerName, stub);

            registry = LocateRegistry.getRegistry(masterIpAddress);
            Master master_stub = (Master) registry.lookup("Master");
            // Get worker's ip address
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            // Register worker
            master_stub.registerWorker(id, name, ipAddress);

            err.println("Worker ready");
        } catch (RemoteException e) {
            err.println("Remote exception: " + e.toString());
            e.printStackTrace();
        }
        catch (AlreadyBoundException | NotBoundException e) {
            err.println("Problem while binding: " + e.toString());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            err.println("Problem while trying to find current host: " + e.toString());
        } catch (UnsupportedEncodingException e) {
            err.println("Problem at Master with encoding");
        } catch (DatabaseException e) {
            err.println(String.format
                    ("Encountered database exception while trying to initialize worker config: %s", e.toString()));
            e.printStackTrace();
        }
    }

    // Remote Method: Will be called by client driver after getting reference to this worker
    public WorkerResult get(String key) throws DatabaseException, UnsupportedEncodingException {
        System.out.println("Worker " + name + " received get request for key " + key);
        DatabaseEntry gotValue = new DatabaseEntry();
        OperationStatus opStatus = workerDb.get(null, new DatabaseEntry(key.getBytes("UTF-8")), gotValue, LockMode.DEFAULT); // Might throw database exception
        boolean opStatusb = false;
        if (gotValue.getData() == null) {
            err.println("Key " + key + " doesn't exist in worker " + name);
            return new WorkerResult(opStatusb, false, null);
        }
        if (OperationStatus.SUCCESS == opStatus) opStatusb = true;
        try {
            return new WorkerResult(opStatusb, true, new String(gotValue.getData(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new WorkerResult(opStatusb, true, null);
        }
    }
    // Remote Method: Will be called by client driver after getting reference to this worker
    public WorkerResult set(String key, String value) throws DatabaseException, UnsupportedEncodingException {
        System.out.println("Worker " + name + " received set request for key " + key
                + " with value " + value);
        OperationStatus opStatus = workerDb.put(null, new DatabaseEntry(key.getBytes("UTF-8")), new DatabaseEntry(value.getBytes("UTF-8"))); // Might throw database exception
        boolean opStatusb = false;
        if (OperationStatus.SUCCESS == opStatus) opStatusb = true;
        workerDb.sync();
        return new WorkerResult(opStatusb, true, null);
    }
}
