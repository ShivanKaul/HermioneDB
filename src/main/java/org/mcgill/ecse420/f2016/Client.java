package org.mcgill.ecse420.f2016;

import org.mcgill.ecse420.f2016.Result.ComputationResult;
import org.mcgill.ecse420.f2016.Result.MasterResult;
import org.mcgill.ecse420.f2016.Result.PromptResult;
import org.mcgill.ecse420.f2016.Result.WorkerResult;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.mcgill.ecse420.f2016.Result.PromptResult.Method.GET;
import static org.mcgill.ecse420.f2016.Result.PromptResult.Method.SET;

public class Client {

    static Registry registry;
    // The cache is of composite key to Computation Result
    // Composite key : table_key
    // ComputationResult : describes the 'hashing' object returned by the Master
    static Map<String, ComputationResult> cache = new HashMap<>();

    public static void main(String[] args) {

        // What would you like to do?
        // get key
        // set key value

        String host = (args.length < 1) ? null : args[0];

        Master masterStub = null;
        try {
            registry = LocateRegistry.getRegistry(host);
        } catch (Exception e) {
            System.out.println("Error while trying to get registry from host " + host + ": " + e.getMessage());
            System.exit(1);
        }
        try {
            masterStub = (Master) registry.lookup("Master");
        } catch (Exception e) {
            System.out.println("Error finding Master in registry from client: " + e.toString());
        }
        // prompt loop
        boolean loop = true;
        while (loop) {
            try {
                PromptResult promptResult = prompt(masterStub);
                if (!promptResult.continueLoop()) {
                    loop = false;
                    continue;
                }

                WorkerResult workerResult = promptResult.workerResult();
                MasterResult masterResult = promptResult.masterResult();
                // Master result is only null if cache was used
                if (masterResult != null && !masterResult.noErrors()) {
                    System.err.println("Master was called and it errored out");
                    if (!masterResult.masterStatus() && !masterResult.dbStatus()) {
                        System.out.println("Master said it does not know of any such table!");
                    }
                }
                if (workerResult.noErrors()) {
                    // If set was the method and there's no errors but no returned value
                    if (promptResult.method() == SET
                            && workerResult.getReturnedValue() == null) {
                        System.out.println("Successfully set!");
                        continue;
                    }
                    System.out.println("response: " + workerResult.getReturnedValue());
                } else if (!masterResult.noErrors()) {
                    System.err.println("Master response had errors: master db status is "
                            + masterResult.dbStatus() + " and master status is "
                            + masterResult.masterStatus());
                } else if (!workerResult.workerStatus()
                        && !workerResult.dbStatus()
                        && promptResult.method() == GET) {
                    System.err.println("Key does not exist");
                } else {
                    System.err.println("Worker response had errors: worker db status is "
                            + workerResult.dbStatus() + " and worker status is "
                            + workerResult.workerStatus());
                }
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("resource")
    private static PromptResult prompt(Master stub) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to do? " +
                "\n 1. `get <table> <key>` " +
                "\n 2. `set <table> <key> <value>` " +
                "\n 3. exit");
        String input = scanner.nextLine().toLowerCase();
        // Check if input is get or set
        String[] tokens = input.split("\\s");
        PromptResult promptResult = null;
        if (tokens[0].equals("get")) {
            if (tokens.length != 3) {
                System.err.println("get takes in only 2 arguments - table name and key");
            }
            promptResult = handleGet(tokens[1], tokens[2], stub);
        } else if (tokens[0].equals("set")) {
            if (tokens.length != 4) {
                System.err.println("set takes in only 3 arguments - table name, key and value");
            }
            promptResult = handleSet(tokens[1], tokens[2], tokens[3], stub);
        } else if (tokens[0].equals("exit")) {
            promptResult = new PromptResult(false);
        }
        return promptResult;
    }

    private static PromptResult handleGet(String table, String key, Master master_stub) throws Exception {
        // Get a handle to the worker from the master
        // Talk to the worker directly
        String compositeKey = table + "_" + key;
        if (!cache.containsKey(compositeKey)) {
            MasterResult masterResult = master_stub.getWorkerHost(compositeKey);
            if (masterResult.noErrors()) {
                ComputationResult computationResult = masterResult.getComputation().lookUpWorker();
                String workerAddress = computationResult.getWorkerIpAddress();
                int workerId = computationResult.getId();
                Worker worker = getWorkerFromAddress(workerAddress, table, workerId);
                WorkerResult workerResult = worker.get(key);
                if (workerResult.noErrors()) {
                    // Put in cache
                    cache.put(compositeKey, computationResult);
                }
                return new PromptResult(workerResult, masterResult, GET);
            } else return new PromptResult(null, masterResult, GET);
        } else { // cache does contain key
            System.out.println("Composite key " + compositeKey + " exists in cache...");
            ComputationResult computationResult = cache.get(compositeKey);
            String workerAddress = computationResult.getWorkerIpAddress();
            int workerId = computationResult.getId();
            Worker worker;
            try {
                worker = getWorkerFromAddress(workerAddress, table, workerId);
            } catch (Exception e) {
                // failed, just get key from master
                MasterResult masterResult = master_stub.getWorkerHost(compositeKey);
                if (masterResult.noErrors()) {
                    computationResult = masterResult.getComputation().lookUpWorker();
                    workerAddress = computationResult.getWorkerIpAddress();
                    workerId = computationResult.getId();
                    worker = getWorkerFromAddress(workerAddress, table, workerId);
                    WorkerResult workerResult = worker.get(key);
                    if (workerResult.noErrors()) {
                        // Put in cache
                        cache.put(compositeKey, computationResult);
                    }
                    return new PromptResult(workerResult, masterResult, GET);
                } else return new PromptResult(null, masterResult, GET);
            }
            WorkerResult workerResult = worker.get(key);
            return new PromptResult(workerResult, null, GET);
        }
    }

    private static Worker getWorkerFromAddress(String workerAddress, String tableName, int id)
            throws RemoteException, NotBoundException {
        registry = LocateRegistry.getRegistry(workerAddress);
        return (Worker) registry.lookup(String.format("Worker_%s_%d", tableName, id));
    }

    private static PromptResult handleSet(String table, String key, String value, Master master_stub) throws Exception {
        // Get a handle to the worker from the master
        // Talk to the worker directly
        String compositeKey = table + "_" + key;
        if (!cache.containsKey(compositeKey)) {
            MasterResult masterResult = master_stub.getWorkerHost(compositeKey);
            if (masterResult.noErrors()) {
                ComputationResult computationResult = masterResult.getComputation().lookUpWorker();
                String workerAddress = computationResult.getWorkerIpAddress();
                int workerId = computationResult.getId();
                Worker worker = getWorkerFromAddress(workerAddress, table, workerId);
                WorkerResult workerResult = worker.set(key, value);
                if (workerResult.noErrors()) {
                    // Put in cache
                    cache.put(compositeKey, computationResult);
                }
                return new PromptResult(workerResult, masterResult, SET);
            } else return new PromptResult(null, masterResult, SET);
        } else { // cache does contain key
            System.out.println("Composite key " + compositeKey + " exists in cache...");
            ComputationResult computationResult = cache.get(compositeKey);
            String workerAddress = computationResult.getWorkerIpAddress();
            int workerId = computationResult.getId();
            Worker worker;
            try {
                worker = getWorkerFromAddress(workerAddress, table, workerId);
            } catch (Exception e) {
                // failed, just get key from master
                MasterResult masterResult = master_stub.getWorkerHost(compositeKey);
                if (masterResult.noErrors()) {
                    computationResult = masterResult.getComputation().lookUpWorker();
                    workerAddress = computationResult.getWorkerIpAddress();
                    workerId = computationResult.getId();
                    worker = getWorkerFromAddress(workerAddress, table, workerId);
                    WorkerResult workerResult = worker.set(key, value);
                    if (workerResult.noErrors()) {
                        // Put in cache
                        cache.put(compositeKey, computationResult);
                    }
                    return new PromptResult(workerResult, masterResult, SET);
                } else return new PromptResult(null, masterResult, SET);
            }
            WorkerResult workerResult = worker.set(key, value);
            return new PromptResult(workerResult, null, SET);
        }
    }
}