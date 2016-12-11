package org.mcgill.ecse420.f2016;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sleepycat.je.DatabaseException;

import org.mcgill.ecse420.f2016.Result.MasterResult;
import org.mcgill.ecse420.f2016.Result.WorkerResult;

public interface Master extends Remote {
    MasterResult getWorkerHost(String key) throws DatabaseException, RemoteException, WrongKeyFormatException;
    void registerWorker(int id, String tableName, String ipAddress) throws RemoteException, UnsupportedEncodingException;
    // get list of tables for client
}
