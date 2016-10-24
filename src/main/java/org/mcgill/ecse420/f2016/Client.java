package org.mcgill.ecse420.f2016;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by shivan on 2016-10-06.
 */
public class Client {

  private Client() {
  }

  public static void main(String[] args) {

    String host = (args.length < 1) ? null : args[0];
    try {
      Registry registry = LocateRegistry.getRegistry(host);
      Master stub = (Master) registry.lookup("Master");
      String response = stub.sayHello();
      System.out.println("response: " + response);
    } catch (Exception e) {
      System.err.println("Client exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
