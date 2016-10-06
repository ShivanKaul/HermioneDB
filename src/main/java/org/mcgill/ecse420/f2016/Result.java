package org.mcgill.ecse420.f2016;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;

public class Result {

    public final OperationStatus dbStatus;
    public final WorkerPoolStatus workerPoolStatus;
    public final DatabaseEntry returnedValue;

    public Result(OperationStatus opStatus, WorkerPoolStatus poolStatus, DatabaseEntry value) {
        this.dbStatus = opStatus;
        this.workerPoolStatus = poolStatus;
        this.returnedValue= value;
    }

    public enum WorkerPoolStatus {
        SUCCESS, DBNOTFOUND
    }
}