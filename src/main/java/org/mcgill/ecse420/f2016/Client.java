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

  private Client() {
  }

  public static void main(String[] args) {

    // What would you like to do?
    // get key
    // set key value

    String host = (args.length < 1) ? null : args[0];



    try {
      Registry registry = LocateRegistry.getRegistry(host);
      Master stub = (Master) registry.lookup("Master");
    } catch (Exception e) {
      System.out.println("Error finding Master in registry from client: " + e.toString());
    }
      // prompt loop
      String response = prompt();
      System.out.println("response: " + response);
    } catch (Exception e) {
      System.err.println("Client exception: " + e.toString());
      e.printStackTrace();
    }
  }

  private static String prompt(Master stub) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("What would you like to do? (syntax : `get <key>` or `set <key> <value>`");
    String input = scanner.next().toLowerCase();
    // Check if input is get or set
    String[] tokens = input.split("\\s");
    String returnValue = null;
    if (tokens[0].equals("get")) {
      if (tokens.length != 2) {
        throw new IllegalArgumentException("get takes in only 2 arguments");
      }
      returnValue = handleGet(tokens[1], stub);
    } else if (tokens[0].equals("set")) {
      if (tokens.length != 3) {
        throw new IllegalArgumentException("set takes in only 3 arguments");
      }
      returnValue = handleSet(tokens[1], tokens[2], stub);
    }
    return returnValue;
  }

  private static String handleGet(String key, Master stub) throws  {

    try {
      DatabaseEntry keyEntry =
              new DatabaseEntry(key.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {

    String response = stub.getWorkerHost(key);

  }
  private static String handleSet(String key, String value, Master stub) {
    String response = stub.sayHello();

  }

}
