package org.mcgill.ecse420.f2016.Result;

import java.io.Serializable;

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
