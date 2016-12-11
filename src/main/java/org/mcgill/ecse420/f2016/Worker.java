package org.mcgill.ecse420.f2016;


import com.sleepycat.je.DatabaseException;

import org.mcgill.ecse420.f2016.Result.WorkerResult;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Worker extends Remote {
    WorkerResult get(String key) throws DatabaseException, RemoteException, UnsupportedEncodingException;
    WorkerResult set(String key, String value) throws DatabaseException, RemoteException, UnsupportedEncodingException;
}