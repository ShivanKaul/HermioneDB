package org.mcgill.ecse420.f2016;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;
import org.mcgill.ecse420.f2016.Configs.MasterConfig;
import org.mcgill.ecse420.f2016.Configs.WorkerConfig;

public class Master {
    private WorkerPool workerPool;
    private MasterDb masterDb;

    public Master(int poolSize) throws DatabaseException {
        // Config DB for workers
        // Environment for workers
        EnvironmentConfig envConfigWorker = new EnvironmentConfig();
        envConfigWorker.setAllowCreate(true);
        // DB config for workers
        DatabaseConfig dbConfigWorker = new DatabaseConfig();
        dbConfigWorker.setAllowCreate(true);
        dbConfigWorker.setSortedDuplicates(false);
        // Set worker config
        WorkerConfig workerConfig = new WorkerConfig(envConfigWorker, dbConfigWorker);
        // Worker pool
        workerPool = new WorkerPool(poolSize, workerConfig, "default");

        // Config DB for Master
        // Environment for master
        EnvironmentConfig envConfigMaster = new EnvironmentConfig();
        envConfigMaster.setAllowCreate(true);
        // DB config for master
        DatabaseConfig dbConfigMaster = new DatabaseConfig();
        dbConfigMaster.setAllowCreate(true);
        dbConfigMaster.setSortedDuplicates(false);
        // Set master config
        MasterConfig masterConfig = new MasterConfig(envConfigMaster, dbConfigMaster);
        masterDb = new MasterDb(masterConfig);

    }
}
