package org.mcgill.ecse420.f2016;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Master extends Remote {
  String sayHello() throws RemoteException;
  Result getWorkerHost(DatabaseEntry key) throws DatabaseException, RemoteException, WrongKeyFormatException;
//  Result set(DatabaseEntry key, DatabaseEntry value) throws DatabaseException, RemoteException;
}
