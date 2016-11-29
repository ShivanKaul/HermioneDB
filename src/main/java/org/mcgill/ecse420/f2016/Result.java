package org.mcgill.ecse420.f2016;

import java.io.Serializable;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;

public class Result implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public OperationStatus dbStatus = OperationStatus.SUCCESS;
    public WorkerPoolStatus workerPoolStatus = WorkerPoolStatus.SUCCESS;
    public final DatabaseEntry returnedValue;

    public Result(OperationStatus opStatus, WorkerPoolStatus poolStatus, DatabaseEntry value) {
        this.dbStatus = opStatus;
        this.workerPoolStatus = poolStatus;
        this.returnedValue= value;
    }

    public enum WorkerPoolStatus {
        SUCCESS, DBNOTFOUND
    }

    public boolean noErrors() {
        return this.dbStatus == OperationStatus.SUCCESS && this.workerPoolStatus == WorkerPoolStatus.SUCCESS;
    }
}