package org.mcgill.ecse420.f2016;


import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Worker extends Remote {
    Result get(String key) throws DatabaseException, RemoteException, UnsupportedEncodingException;
    Result set(String key, String value) throws DatabaseException, RemoteException, UnsupportedEncodingException;
}