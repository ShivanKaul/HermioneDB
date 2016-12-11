package org.mcgill.ecse420.f2016;

import com.sleepycat.je.DatabaseException;

import org.mcgill.ecse420.f2016.Result.MasterResult;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Master Remote interface : used by Client and Worker to call remote methods
 */
public interface Master extends Remote {
    MasterResult getWorkerHost(String key) throws DatabaseException, RemoteException, WrongKeyFormatException;
    void registerWorker(int id, String tableName, String ipAddress) throws RemoteException, UnsupportedEncodingException;
}
