package org.mcgill.ecse420.f2016;

import org.mcgill.ecse420.f2016.Result.ComputationResult;

import java.io.Serializable;
import java.util.Map;

import static org.mcgill.ecse420.f2016.MasterImpl.RING_SIZE;

public class WorkerLookupComputation implements Serializable {

    private Map<String, Integer> ring = null;
    private String key = null;

    // Ring is a mapping of IP address to a UUID for a Worker
    // Master seeds the WorkerLookupComputation before sending it to Client
    WorkerLookupComputation(Map<String, Integer> ring, String key) {
        this.ring = ring;
        this.key = key;
    }

    // Returns worker IP address + id in the form of a computation
    ComputationResult lookUpWorker() {
        int id = Integer.MAX_VALUE;
        int minDelta = Integer.MAX_VALUE;
        int smallestKey = Integer.MAX_VALUE;
        int hashedKey = Math.abs(key.hashCode()) % RING_SIZE;
        System.out.println(String.format("DEBUG : Key %s hashes to %d", key, hashedKey));
        String smallestIpAddress = null;
        String workerIpAddress = null;
        // Traverse the ring and pick which bucket the hashed key should fall into
        // If the hashed key is smaller than any available bucket, then put it in the smallest
        // bucket
        for (String ipAddress : ring.keySet()) {
            int curKey = ring.get(ipAddress);
            if (smallestKey > curKey) {
                smallestIpAddress = ipAddress;
                smallestKey = curKey;
            }
            if (hashedKey - curKey >= 0 && hashedKey - curKey <= minDelta) {
                minDelta = hashedKey - curKey;
                workerIpAddress = ipAddress;
                id = curKey;
            }
        }
        if (workerIpAddress == null) {
            workerIpAddress = smallestIpAddress;
            id = smallestKey;
        }
        return new ComputationResult(id, workerIpAddress);
    }

}



