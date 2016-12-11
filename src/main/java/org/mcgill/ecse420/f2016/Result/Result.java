package org.mcgill.ecse420.f2016.Result;

import java.io.Serializable;

/**
 * Abstract class Result that is the super class of MasterResult and WorkerResult.
 */
public abstract class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    protected boolean dbStatus = true;

    public Result(boolean opStatus) {
        this.dbStatus = opStatus;
    }

    public boolean dbStatus() {
        return this.dbStatus;
    }

    public abstract boolean noErrors();
}