package org.mcgill.ecse420.f2016;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.mcgill.ecse420.f2016.Configs.MasterConfig;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class MasterImpl implements Master {
    
    //TODO Add a capability to dynamically assign worker addresses
    private static final String WORKER_NAME_FOR_CUSTOMER = "customer";
    private static final String WORKER_NAME_FOR_EMPLOYEE = "employee";
    private static final String WORKER_NAME_FOR_OTHERS = "others";
    
    private MasterDb masterDb;
    private DatabaseEntry customerKey;
    private DatabaseEntry employeeKey;
    private DatabaseEntry otherKey;
    private DatabaseEntry worker1;
    private DatabaseEntry worker2;
    private DatabaseEntry worker3;

    public MasterImpl(int poolSize) throws DatabaseException {
        EnvironmentConfig envConfigWorker = new EnvironmentConfig();
        envConfigWorker.setAllowCreate(true);
        // DB config for workers
        DatabaseConfig dbConfigWorker = new DatabaseConfig();
        dbConfigWorker.setAllowCreate(true);
        dbConfigWorker.setSortedDuplicates(false);
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
        try {
            customerKey = new DatabaseEntry("customer".getBytes("UTF-8"));
            employeeKey = new DatabaseEntry("employee".getBytes("UTF-8"));
            otherKey = new DatabaseEntry("other".getBytes("UTF-8"));
            worker1 =
                    new DatabaseEntry(WORKER_NAME_FOR_CUSTOMER.getBytes("UTF-8"));
            worker2 =
                    new DatabaseEntry(WORKER_NAME_FOR_EMPLOYEE.getBytes("UTF-8"));
            worker3 = new DatabaseEntry(WORKER_NAME_FOR_OTHERS.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initMetadata();
    }

    private void initMetadata() {
        Database db = masterDb.getDB();
        try {
            if (db.get(null, customerKey, new DatabaseEntry(), LockMode.DEFAULT)
                    .equals(OperationStatus.NOTFOUND)) {
                // metadata for customer worker is not set yet. set it here
                db.put(null, customerKey, worker1);
            }
            if (db.get(null, employeeKey, new DatabaseEntry(), LockMode.DEFAULT)
                    .equals(OperationStatus.NOTFOUND)) {
                // metadata for employee worker is not set yet. set it here
                db.put(null, employeeKey, worker2);
            }
            if (db.get(null, otherKey, new DatabaseEntry(), LockMode.DEFAULT)
                    .equals(OperationStatus.NOTFOUND)) {
                // metadata for employee worker is not set yet. set it here
                db.put(null, otherKey, worker3);
            }
            // TODO add 'other' type
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
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

    @Override
    public Result getWorkerHost(String k)
            throws DatabaseException, RemoteException, WrongKeyFormatException {
        Database db = masterDb.getDB();
        DatabaseEntry result = new DatabaseEntry();
        if (!k.matches("^[a-z]+_\\d+$")) {
            // Invalid format.
            throw new WrongKeyFormatException(
                    "Wrong key format. The key should start with the type followed by the index number");
        }
        if (k.startsWith("customer")) {
            // return customer related worker
            OperationStatus ops =
                    db.get(null, customerKey, result, LockMode.DEFAULT);
            return new Result(ops, null, result);
        } else if (k.startsWith("employee")) {
            // return employee related worker
            OperationStatus ops =
                    db.get(null, employeeKey, result, LockMode.DEFAULT);
            return new Result(ops, null, result);
        } else {
            // return or a worker that takes any other type of data
            OperationStatus ops = db.get(null, otherKey, result, LockMode.DEFAULT);
            return new Result(ops, null, result);
        }
    }
}
