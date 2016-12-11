package org.mcgill.ecse420.f2016;

import org.json.JSONArray;
import org.json.JSONException;
import org.mcgill.ecse420.f2016.Result.ComputationResult;

import java.io.InterruptedIOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.mcgill.ecse420.f2016.MasterImpl.RING_SIZE;

public class WorkerLookupComputation implements Serializable {

    private Map<String, Integer> ring = null;
    private String key = null;

    WorkerLookupComputation(Map<String, Integer> ring, String key) {
        this.ring = ring;
        this.key = key;
    }

    // Returns worker IP address
    ComputationResult lookUpWorker() throws JSONException {
        int id = Integer.MAX_VALUE;
        int minDelta = Integer.MAX_VALUE;
        int smallestKey = Integer.MAX_VALUE;
        int hashedKey = Math.abs(key.hashCode()) % RING_SIZE;
        String smallestIpAddress = null;
        String workerIpAddress = null;
        // Traverse the ring
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



