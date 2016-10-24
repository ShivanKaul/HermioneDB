package org.mcgill.ecse420.f2016;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Master extends Remote {
  String sayHello() throws RemoteException;
}
