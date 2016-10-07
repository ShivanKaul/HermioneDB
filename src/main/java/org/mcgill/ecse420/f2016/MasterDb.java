package org.mcgill.ecse420.f2016;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import org.mcgill.ecse420.f2016.Configs.MasterConfig;

public class MasterDb {

    private Database db;

    public MasterDb(MasterConfig config) throws DatabaseException {
        db = config.environment.openDatabase(null, "masterDb", config.dbConfig);
    }
}
