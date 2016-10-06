package org.mcgill.ecse420.f2016;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.je.LockMode;

public class Master {

  public static void main(String[] args) {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    Environment dbEnv;
    try {
      dbEnv = new Environment(
          new File("/tmp"), envConfig);
      DatabaseConfig dbConfig = new DatabaseConfig();
      dbConfig.setAllowCreate(true);
      dbConfig.setSortedDuplicates(false);
      Database db = dbEnv.openDatabase(null, "SampleDB", dbConfig);
      DatabaseEntry searchEntry = new DatabaseEntry();
      DatabaseEntry dataValue =
          new DatabaseEntry(" data content".getBytes("UTF-8"));
      DatabaseEntry keyValue =
          new DatabaseEntry("key content".getBytes("UTF-8"));
      db.put(null, keyValue, dataValue);// inserting an entry
      db.get(null, keyValue, searchEntry, LockMode.DEFAULT);// retrieving record
      String foundData = new String(searchEntry.getData(), "UTF-8");
      dataValue = new DatabaseEntry("updated data content".getBytes("UTF-8"));
      db.put(null, keyValue, dataValue);// updating an entry
      db.delete(null, keyValue);// delete operation
      db.close();
      dbEnv.close();
    } catch (EnvironmentLockedException e) {
      e.printStackTrace();
    } catch (DatabaseException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
