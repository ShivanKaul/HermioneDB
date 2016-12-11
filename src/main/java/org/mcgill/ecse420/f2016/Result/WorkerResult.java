package org.mcgill.ecse420.f2016.Result;

import java.io.Serializable;

/**
 * Used in communication between a Worker and Client.
 * Serializable because it has to be sent over the network.
 * Holds a tuple of statuses and a returned value. This will be null if a SET was done
 */
public class WorkerResult extends Result implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private boolean workerStatus = true;
    private final String returnedValue;

    public WorkerResult(boolean opStatus, boolean workerStatus, String value) {
        super(opStatus);
        this.workerStatus = workerStatus;
        this.returnedValue = value;
    }

    public boolean workerStatus() {
        return this.workerStatus;
    }

    public String getReturnedValue() {
        return this.returnedValue;
    }

    public boolean noErrors() {
        return this.dbStatus && this.workerStatus;
    }
}