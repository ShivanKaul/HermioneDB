package org.mcgill.ecse420.f2016.Result;

import java.io.Serializable;

public class WorkerResult extends Result implements Serializable {

    /**
     * 
     */
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