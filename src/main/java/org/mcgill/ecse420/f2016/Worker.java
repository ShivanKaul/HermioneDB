package org.mcgill.ecse420.f2016;


import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Worker extends Remote {
    Result get(DatabaseEntry key) throws DatabaseException, RemoteException;
    Result set(DatabaseEntry key, DatabaseEntry value) throws DatabaseException, RemoteException;
}