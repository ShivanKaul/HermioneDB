package org.mcgill.ecse420.f2016;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcgill.ecse420.f2016.Configs.MasterConfig;
import org.mcgill.ecse420.f2016.Result.MasterResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Master : Stores metadata about the tables and workers.
 */
public class MasterImpl implements Master {

    public static final int RING_SIZE = 1000;

    private MasterDb masterDb;

    public MasterImpl() throws DatabaseException {
        EnvironmentConfig envConfigMaster = new EnvironmentConfig();
        envConfigMaster.setAllowCreate(true);
        // DB config for master
        DatabaseConfig dbConfigMaster = new DatabaseConfig();
        dbConfigMaster.setAllowCreate(true);
        dbConfigMaster.setSortedDuplicates(false);
        File dir = new File("/tmp/master");
        // Attempt to create the directory here
        if (!dir.exists() && !dir.mkdir()) {
            System.out.println("Failed trying to create directory /tmp/master, " +
                    "please make sure you have access, " +
                    "BerkeleyDDB cannot function without creating these directories");
            System.exit(1);
        }
        // Set master config
        MasterConfig masterConfig =
                new MasterConfig(envConfigMaster, dbConfigMaster);
        masterDb = new MasterDb(masterConfig);
    }

    /**
     * Store a Worker's IP address and UUID in Master's db instance.
     *
     * @param id : worker UUID
     * @param tableName : worker's table
     * @param address : worker's IP address
     */
    private void storeWorkerInMasterDb(int id, DatabaseEntry tableName, String address) {
        Database db = masterDb.getDB();
        // Generate unique reference to worker
        // Store in DB
        try {
            JSONObject value = new JSONObject().put("id", id).put("address", address);
            System.out.println("Json object looks like " + value.toString());
            DatabaseEntry got = new DatabaseEntry();
            if (db.get(null, tableName, got, LockMode.DEFAULT)
                    .equals(OperationStatus.NOTFOUND)) {
                // metadata for table is not set yet. set it here
                JSONArray ring = new JSONArray();
                ring.put(value);
                System.out.println("Table does not exist, adding...");
                System.out.println("Ring looks like: " + ring.toString());
                db.put(null, tableName, new DatabaseEntry(ring.toString().getBytes("UTF-8")));
            } else {
                JSONArray ring = new JSONArray(new String(got.getData(), "UTF-8"));
                ring.put(value);
                System.out.println("Table exists, appending...");
                System.out.println("Ring looks like: " + ring.toString());
                db.put(null, tableName, new DatabaseEntry(ring.toString().getBytes("UTF-8")));
            }
            db.sync();
        } catch (DatabaseException | JSONException | UnsupportedEncodingException e) {
            System.out.println("Error while trying to store worker in master db");
            e.printStackTrace();
        }
    }

    /**
     * Called by Worker when it starts up.
     *
     * @param id : worker UUID
     * @param tableName : worker table
     * @param ipAddress : worker IP address
     * @throws RemoteException
     * @throws UnsupportedEncodingException
     */
    @Override
    public void registerWorker(int id, String tableName, String ipAddress)
            throws RemoteException, UnsupportedEncodingException {
        // put in db
        System.out.println("Received request to add worker "
                + tableName
                + " for ip address " + ipAddress);
        DatabaseEntry table = new DatabaseEntry(tableName.getBytes("UTF-8"));
        storeWorkerInMasterDb(id, table, ipAddress);
    }

    public static void main(String args[]) {
        try {
            MasterImpl obj = new MasterImpl();
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

    /**
     * Called by Client to figure out which worker to talk to, given a key.
     *
     * @param k : key
     * @return
     * @throws DatabaseException
     * @throws RemoteException
     * @throws WrongKeyFormatException
     */
    @Override
    public MasterResult getWorkerHost(String k)
            throws DatabaseException, RemoteException, WrongKeyFormatException {
        Database db = masterDb.getDB();
        DatabaseEntry result = new DatabaseEntry();
        System.out.println("Master received worker address request for key " + k);

        boolean dbStatus = false;
        boolean masterStatus = false;
        // Get table
        String[] composite_key = k.split("_");
        String tableName = composite_key[0];
        String key = composite_key[1];

        try {
            DatabaseEntry got = new DatabaseEntry();
            if (db.get(null, new DatabaseEntry(tableName.getBytes("UTF-8")), got, LockMode.DEFAULT)
                    .equals(OperationStatus.SUCCESS)) {
                // send worker lookup computation
                JSONArray jsonRing = new JSONArray(new String(got.getData(), "UTF-8"));
                // Convert json array to hashmap :  ip address to id
                Map<String, Integer> ring = new HashMap<>();
                for (int i = 0; i < jsonRing.length(); i++) {
                    ring.put(jsonRing.getJSONObject(i).getString("address"),
                            jsonRing.getJSONObject(i).getInt("id"));
                }
                return new MasterResult(true, true, new WorkerLookupComputation(ring, key));
            } else {
                System.out.println("Master does not have a reference " +
                        "to any worker that supports that table!");
            }
            // The idea is that if the table does not exist in Master, then it is both a db fail
            // as well as a master fail
        } catch (DatabaseException e) {
            e.printStackTrace();
            masterStatus = true;
        } catch (JSONException  | UnsupportedEncodingException e) {
            e.printStackTrace();
            dbStatus = true;
        }
        return new MasterResult(dbStatus, masterStatus, null);
    }
}
