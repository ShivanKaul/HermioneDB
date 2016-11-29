package org.mcgill.ecse420.f2016;

import com.sleepycat.je.DatabaseEntry;

import java.io.UnsupportedEncodingException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


/**
 * Created by shivan on 2016-10-06.
 */
public class Client {

  static Registry registry;

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
        if (response.noErrors()) {
          System.out.println("response: " + response.returnedValue);
        } else {
          System.out.println("Response in Client had errors: db status is "
                  + response.dbStatus.toString() + " and worker status is "
                  + response.workerPoolStatus.toString());
        }
      } catch (Exception e) {
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }
  }

  private static PromptResult prompt(Master stub) throws Exception {
    Scanner scanner = new Scanner(System.in);
    System.out.println("What would you like to do? \n 1. `get <table> <key>` \n 2. `set <table> <key> <value>` \n 3. exit");
    String input = scanner.next().toLowerCase();
    // Check if input is get or set
    String[] tokens = input.split("\\s");
    PromptResult promptResult = null;
    if (tokens[0].equals("get")) {
      if (tokens.length != 3) {
        throw new IllegalArgumentException("get takes in only 2 arguments - table name and key");
      }
      promptResult = new PromptResult((handleGet(tokens[1], tokens[2], stub)));
    } else if (tokens[0].equals("set")) {
      if (tokens.length != 4) {
        throw new IllegalArgumentException("set takes in only 3 arguments - table name, key and value");
      }
      promptResult = new PromptResult((handleSet(tokens[1], tokens[2], tokens[3], stub)));
    } else if (tokens[0].equals("exit")) {
      promptResult = new PromptResult(false);
    }
    return promptResult;
  }

  private static Result handleGet(String table, String key, Master stub) throws Exception {
    // Get a handle to the worker from the master
    // Talk to the worker directly
    DatabaseEntry keyEntry =
            new DatabaseEntry(key.getBytes("UTF-8"));

    Result response = stub.getWorkerHost(table + "_" + key);
    if (response.noErrors()) {
      String workerName = new String(response.returnedValue.getData(), "UTF-8");
      // Connect to worker
      Worker worker = (Worker) registry.lookup(workerName);
      return worker.get(keyEntry);
    } else return response;
  }
  private static Result handleSet(String table, String key, String value, Master stub) {
    Result response = stub.sayHello();

  }

}

class PromptResult {
  private Result result = null;
  private boolean continueLoop = true;

  public PromptResult(Result result) {
    this.result = result;
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

}