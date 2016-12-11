package org.mcgill.ecse420.f2016.Result;

import java.io.Serializable;

/**
 * Wrapper over the return values of WorkerLookupComputation. If Java had tuples we would not need
 * this...
 */
public class ComputationResult implements Serializable {
    private int id;
    private String workerIpAddress;

    public ComputationResult(int id, String workerIpAddress) {
        this.id = id;
        this.workerIpAddress = workerIpAddress;
    }

    public int getId() {
        return id;
    }

    public String getWorkerIpAddress() {
        return workerIpAddress;
    }
}
