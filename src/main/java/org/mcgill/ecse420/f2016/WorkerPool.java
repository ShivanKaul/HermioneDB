package org.mcgill.ecse420.f2016;

import com.sleepycat.je.*;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;

import java.util.HashMap;
import java.util.Map;

public class WorkerPool {

    private Map<Integer, Database> dbs = new HashMap<>();

    // Constructor
    public WorkerPool(int poolSize, WorkerConfig config,
                      String namingScheme) throws DatabaseException {


        for (int i = 0; i < poolSize; i++) {
            dbs.put(Integer.valueOf(i), config.environment.openDatabase(null, "worker" + namingScheme + i, config.dbConfig));
        }
    }

    // Will be called by client driver after getting db id from Master
    public Result get(int dbId, DatabaseEntry key) throws DatabaseException {
        if (dbs.containsKey(Integer.valueOf(dbId))) {
            Database db = dbs.get(dbId);
            DatabaseEntry gotValue = new DatabaseEntry();
            OperationStatus opStatus = db.get(null, key, gotValue, LockMode.DEFAULT); // Might throw database exception
            return new Result(opStatus, Result.WorkerPoolStatus.SUCCESS, gotValue);
        } else {
            // DB not found, tell client and let it propagate to Master that it should update
            return new Result(null, Result.WorkerPoolStatus.DBNOTFOUND, null);
        }
    }

    public Result setDbs(int dbId, DatabaseEntry key, DatabaseEntry value) throws DatabaseException {
        if (dbs.containsKey(Integer.valueOf(dbId))) {
            Database db = dbs.get(dbId);
            OperationStatus opStatus = db.put(null, key, value); // Might throw database exception
            return new Result(opStatus, Result.WorkerPoolStatus.SUCCESS, null);
        } else {
            // NOTE: considered simply creating a new BerkeleyDB instance and putting but that would allow client to
            // define the distribution scheme - that should only be handled by Master. So we just return status code and
            // let Master handle
            return new Result(null, Result.WorkerPoolStatus.DBNOTFOUND, null);
        }
    }
}
