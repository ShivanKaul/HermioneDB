package org.mcgill.ecse420.f2016;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

  static Registry registry;
  // We eventually need to cache the hash object that gives us the worker
  // For now we just have simple map from key (dbname + key) to worker IP
  static Map<String, String> cache = new HashMap<>();

  private Client() {
  }

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

        Result response = promptResult.result();
        // If GET and server says doesn't exist
        if (promptResult.method().equals(PromptResult.Method.GET) && !response.workerPoolStatus && !response.dbStatus) {
          System.out.println("Server responded with null! Are you sure the key you're " +
                  "looking for exists?");
          continue;
        }
        if (response.noErrors()) {
          // If set was the method and there's no errors but the no returned value
          if (promptResult.method() == PromptResult.Method.SET && response.returnedValue == null) {
            System.out.println("Successfully set!");
            continue;
          }
          System.out.println("response: " + response.returnedValue);
        } else {
          System.out.println("Response in Client had errors: db status is "
                  + response.dbStatus + " and worker status is "
                  + response.workerPoolStatus);
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
    System.out.println("What would you like to do? \n 1. `get <table> <key>` \n 2. `set <table> <key> <value>` \n 3. exit");
    String input = scanner.nextLine().toLowerCase();
    // Check if input is get or set
    String[] tokens = input.split("\\s");
    PromptResult promptResult = null;
    if (tokens[0].equals("get")) {
      if (tokens.length != 3) {
        throw new IllegalArgumentException("get takes in only 2 arguments - table name and key");
      }
      promptResult = new PromptResult((handleGet(tokens[1], tokens[2], stub)), PromptResult.Method.GET);
    } else if (tokens[0].equals("set")) {
      if (tokens.length != 4) {
        throw new IllegalArgumentException("set takes in only 3 arguments - table name, key and value");
      }
      promptResult = new PromptResult((handleSet(tokens[1], tokens[2], tokens[3], stub)), PromptResult.Method.SET);
    } else if (tokens[0].equals("exit")) {
      promptResult = new PromptResult(false);
    }
    return promptResult;
  }

  private static Result handleGet(String table, String key, Master master_stub) throws Exception {
    // Get a handle to the worker from the master
    // Talk to the worker directly
    String compositeKey = table + "_" + key;
    if (!cache.containsKey(compositeKey)) {
      Result response = master_stub.getWorkerHost(compositeKey);
      if (response.noErrors()) {
        String workerAddress = response.returnedValue;
        Worker worker = getWorkerFromAddress(workerAddress);
        // Put in cache
        cache.put(compositeKey, workerAddress);
        return worker.get(key);
      } else return response;
    } else { // cache does contain key
      String workerAddress = cache.get(compositeKey);
      Worker worker;
      try {
        worker = getWorkerFromAddress(workerAddress);
      } catch (Exception e) {
        // failed, just get key from master
        Result response = master_stub.getWorkerHost(compositeKey);
        if (response.noErrors()) {
          workerAddress = response.returnedValue;
          worker = getWorkerFromAddress(workerAddress);
          // Put in cache
          cache.put(compositeKey, workerAddress);
          return worker.get(key);
        } else return response;
      }
      return worker.get(key);
    }
  }

  private static Worker getWorkerFromAddress(String workerAddress) throws RemoteException, NotBoundException {
    registry = LocateRegistry.getRegistry(workerAddress);
    return (Worker) registry.lookup("Worker");
  }

  private static Result handleSet(String table, String key, String value, Master master_stub) throws Exception {
    // Get a handle to the worker from the master
    // Talk to the worker directly
    String compositeKey = table + "_" + key;
    if (!cache.containsKey(compositeKey)) {
      Result response = master_stub.getWorkerHost(compositeKey);
      if (response.noErrors()) {
        String workerAddress = response.returnedValue;
        Worker worker = getWorkerFromAddress(workerAddress);
        // Put in cache
        cache.put(compositeKey, workerAddress);
        return worker.get(key);
      } else return response;
    } else { // cache does contain key
      String workerAddress = cache.get(compositeKey);
      Worker worker;
      try {
        worker = getWorkerFromAddress(workerAddress);
      } catch (Exception e) {
        // failed, just get key from master
        Result response = master_stub.getWorkerHost(compositeKey);
        if (response.noErrors()) {
          workerAddress = response.returnedValue;
          worker = getWorkerFromAddress(workerAddress);
          // Put in cache
          cache.put(compositeKey, workerAddress);
          return worker.set(key, value);
        } else return response;
      }
      return worker.set(key, value);
    }

  }

}

class PromptResult {
  private Result result = null;
  private Method method;
  private boolean continueLoop = true;

  public enum Method {
    GET, SET
  }

  public PromptResult(Result result, Method method) {
    this.result = result;
    this.method = method;
  }

  public PromptResult(boolean continueLoop) {
    this.continueLoop = continueLoop;
  }

  public boolean continueLoop() {
    return this.continueLoop;
  }

  public Result result() {
    return this.result;
  }

  public Method method() {
    return this.method;
  }

}