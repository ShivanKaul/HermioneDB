package org.mcgill.ecse420.f2016;

import java.io.Serializable;

public class Result implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public boolean dbStatus = true;
    public boolean workerPoolStatus = true;
    public final String returnedValue;

    public Result(boolean opStatus, boolean poolStatus, String value) {
        this.dbStatus = opStatus;
        this.workerPoolStatus = poolStatus;
        this.returnedValue= value;
    }

    public boolean noErrors() {
        return this.dbStatus && this.workerPoolStatus;
    }
}