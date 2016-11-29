package org.mcgill.ecse420.f2016;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sleepycat.je.DatabaseException;

public interface Master extends Remote {
  String sayHello() throws RemoteException;
  Result getWorkerHost(String key) throws DatabaseException, RemoteException, WrongKeyFormatException;
//  Result set(DatabaseEntry key, DatabaseEntry value) throws DatabaseException, RemoteException;
}