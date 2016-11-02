package org.mcgill.ecse420.f2016;

import org.mcgill.ecse420.f2016.Configs.MasterConfig;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;

public class MasterDb {

  private Database db;
  private MasterConfig config;

  public MasterDb(MasterConfig config) throws DatabaseException {
    this.config = config;
    db = config.environment.openDatabase(null, "masterDb", config.dbConfig);
  }

  public Database getDB() {
    return db;
  }

  public void close() throws DatabaseException {
    db.close();
    config.environment.close();
  }
}
